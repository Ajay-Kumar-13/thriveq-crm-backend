package com.thriveq.crm.leads_api.controller;

import com.thriveq.crm.leads_api.model.Lead;
import com.thriveq.crm.leads_api.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leads;

    @GetMapping("/whoami")
    public Mono<Map<String, String>> whoami(ServerWebExchange exchange) {
        return Mono.just(Map.of(
                "x-user-id",    String.valueOf(exchange.getRequest().getHeaders().getFirst("x-user-id")),
                "x-user-roles", String.valueOf(exchange.getRequest().getHeaders().getFirst("x-user-roles"))));
    }

    // No @PreAuthorize. Coarse-grained authz already happened at the proxy.
    @GetMapping("/{id}")
    public Mono<Lead> get(@PathVariable UUID id, Principal principal) {
        return leads.findOwned(id, UUID.fromString(principal.getName()));
    }
}
