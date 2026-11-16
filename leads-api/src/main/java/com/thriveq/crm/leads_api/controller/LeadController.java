package com.thriveq.crm.leads_api.controller;

import com.thriveq.crm.leads_api.model.Lead;
import com.thriveq.crm.leads_api.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import com.thriveq.crm.leads_api.dto.NewLead;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;

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

    @GetMapping("/{id}")
    public Mono<Lead> get(@PathVariable Long id) {
        return leads.findOwned(id);
    }

    @PostMapping
    public Mono<Lead> create(@RequestBody NewLead newLead) {
        return leads.create(newLead);
    }
}
