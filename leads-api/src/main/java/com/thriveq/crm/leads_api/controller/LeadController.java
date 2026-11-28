package com.thriveq.crm.leads_api.controller;

import com.thriveq.crm.leads_api.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import com.thriveq.crm.leads_api.dto.CreateLeadRequest;
import com.thriveq.crm.leads_api.dto.LeadsResponse;

import reactor.core.publisher.Mono;

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

    @GetMapping("/{id}")
    public Mono<LeadsResponse> get(@PathVariable UUID id) {
        return leads.findLeadById(id);
    }

    @PostMapping
    public Mono<Void> create(@RequestBody CreateLeadRequest newLead) {
        return leads.createLeadAndSavePayloadInOutbox(newLead);
    }

    @PatchMapping("/{id}")
    public Mono<Void> update(@PathVariable UUID id, @RequestBody CreateLeadRequest updatedLead) {
        return leads.updateLeadAndSavePayloadInOutbox(id, updatedLead);
    }
}
