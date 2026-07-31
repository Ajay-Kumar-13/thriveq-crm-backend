package com.thriveq.crm.external_authorization.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionCache {
    /**
     * DatabaseClient is Spring's lower-level tool for running SQL directly.
     * Why the PDP's cache uses DatabaseClient instead of a repository?
     * Repository — CRUD and simple lookups on one entity. findById, findByEmail, save, delete.
     * DatabaseClient — Multi-table joins, projections, aggregate queries, anything that returns a shape that isn't an entity.
     *                  The cache query is exactly this.
     */
    private final DatabaseClient db;
    /**
     * Writes go straight to main memory — when the scheduler assigns snapshot = next, it's immediately visible to all threads.
     */
    private volatile Map<String, Set<String>> snapshot = Map.of();

    /**
     * @Scheduled doesn't run immediately on boot — with fixedDelay = 30_000 the first run is 30s after startup
     */
    @PostConstruct
    void init() { refresh().block(Duration.ofSeconds(10)); }
    /**
     * The scheduler isn't reactive, so you bridge from the timer into the reactive world manually.
     */
    @Scheduled(fixedDelay = 30_000)
    public void scheduled() {
        refresh().subscribe(
                v -> {},
                e -> log.info("role_permissions refresh FAILED, serving stale", e));
    }

    private Mono<Void> refresh() {
        return db.sql("""
            SELECT r.name AS role, p.name as perm
            FROM auth.role_permissions rp
            JOIN auth.roles r ON r.id = rp.role_id
            JOIN auth.permissions p ON p.id = rp.permission_id
            """
        )
                .fetch()
                .all()
                /** collectMultiMap takes two functions, 1. which extracts the key and 2. which extracts the value.
                 *  this will return you a kind of dictionary, in which all the keys will be mapped to its values.
                 */
                .collectMultimap(row -> (String) row.get("role"), row -> (String) row.get("perm"))
                /**
                 * We can directly .stream() on map, .entrySet() will allow us to perform .stream() on map
                 */
                .doOnNext(m -> {
                    Map<String, Set<String>> next = m.entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, e -> Set.copyOf(e.getValue())));
                    this.snapshot = next;
                })
                .then();
    }

    public Set<String> permissionFor(Collection<String> roles){
        Map<String, Set<String>> s = snapshot;
        return roles.stream()
                .flatMap(r ->  s.getOrDefault(r, Set.of()).stream())
                .collect(Collectors.toSet());
    }
}
