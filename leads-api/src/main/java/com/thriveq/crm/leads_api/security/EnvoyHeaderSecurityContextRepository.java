package com.thriveq.crm.leads_api.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class EnvoyHeaderSecurityContextRepository implements ServerSecurityContextRepository {

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userId = headers.getFirst("x-user-id");
        if (userId == null) return Mono.empty();

        String roleHeader = headers.getFirst("x-user-roles");
        List<GrantedAuthority> authorities = roleHeader == null ? List.of() :
                Arrays.stream(roleHeader.split(","))
                        .map(String::trim)
                        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_"+role))
                        .toList();

        return Mono.just(new SecurityContextImpl(new UsernamePasswordAuthenticationToken(userId, null, authorities)));
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext c) {
        return Mono.empty();
    }

}
