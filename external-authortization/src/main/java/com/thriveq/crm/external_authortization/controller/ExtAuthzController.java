package com.thriveq.crm.external_authortization.controller;

import com.thriveq.crm.external_authortization.service.RolePermissionCache;
import com.thriveq.crm.external_authortization.util.PolicyEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ExtAuthzController {

    private final PolicyEngine policyEngine;
    private final ReactiveJwtDecoder decoder;
    private final RolePermissionCache cache;
    /**
     * 1. You need control over the status, and ResponseEntity is how you set it in Spring.
            The second reason is headers. On allow, you must return x-user-id and x-user-roles — Envoy copies those upstream
     * 2. @RequestMapping matches all the methods, GET, POST, PUT, PATCH, DELETE
     * */
    @RequestMapping("/authz/**")
    public Mono<ResponseEntity<Void>> check(ServerHttpRequest request) {

        String method = header(request, "x-forwarded-method");
        String rawPath = header(request, "x-forwarded-uri");
        String rid = header(request, "x-request-id");

        String path = rawPath == null ? "/" : rawPath.split("\\?", 2)[0];

        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            log.info("DENY rid={} reason=no_access_token path={}", rid, path);
            return Mono.just(status(401));
        }

        return decoder.decode(auth.substring(7))
                .map(jwt -> {
                    List<String> roles = jwt.getClaimAsStringList("roles");
                    if (roles == null) {
                        roles = List.of();
                    }

                    Optional<String> required =  policyEngine.requiredPermissions(method, path);
                    if (required.isEmpty()) {
                        log.info("DENY rid={} sub={} path=no_rule {} {}", rid, jwt.getSubject(), method, path);
                        return status(403);
                    }

                    Set<String> granted = cache.permissionFor(roles);
                    if (!granted.contains(required.get())) {
                        log.info("DENY rid={} sub={} roles={} needed={} {} {}",
                                rid, jwt.getSubject(), roles, required.get(), method, path);
                        return status(403);
                    }

                    log.info("ALLOW rid={} sub={} roles={}", rid, jwt.getSubject(), roles);
                    return ResponseEntity.
                            ok()
                            .header("x-user-id", jwt.getSubject())
                            .header("x-user-roles", String.join(",", roles))
                            .<Void>build();
                })
                .onErrorResume(JwtException.class, e-> {
                    log.info("DENY rid={} reason=bad_token: {}", rid, e.getMessage());
                    return Mono.just(status(401));
                });

    }

    private static String header(ServerHttpRequest r, String name) {
        return r.getHeaders().getFirst(name);
    }

    private static ResponseEntity<Void> status(int code) {
        return ResponseEntity.status(code).build();
    }

}