package com.thriveq.crm.external_authorization.service;

import com.thriveq.crm.external_authorization.config.RbacPolicy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
/**
 * CatalogValidator's @PostConstruct needs both RbacPolicy (loaded by your RbacPolicyLoader bean) and PermissionCatalog (needs DatabaseClient, which needs the DB up).
 * Spring handles the bean dependency order via the constructor injection — it won't build CatalogValidator until both are ready.
 */
public class CatalogValidator {
    private final RbacPolicy rbacPolicy;
    private final PermissionCatalog permissionCatalog;

    @PostConstruct
    public void validate() {
        Set<String> inDb = permissionCatalog.allNames();

        Set<String> inPolicy = rbacPolicy
                .rules()
                .stream()
                .map(RbacPolicy.Rule::permission)
                .collect(Collectors.toSet());

        Set<String> missing =  new HashSet<>(inPolicy);
        missing.removeAll(inDb);
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Policy references unknown permissions "+ missing);
        }

        Set<String> orphaned = new HashSet<>(inDb);
        orphaned.removeAll(inPolicy);
        if (!orphaned.isEmpty()) {
            log.info("Permissions in DB guard no endpoint {}", orphaned);
        }
    }
}
