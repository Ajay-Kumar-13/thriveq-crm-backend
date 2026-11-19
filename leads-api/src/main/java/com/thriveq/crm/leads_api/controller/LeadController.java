package com.thriveq.crm.leads_api.controller;

import com.thriveq.crm.leads_api.model.Lead;
import com.thriveq.crm.leads_api.service.CommentService;
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
    private final CommentService comments;

    @GetMapping("/whoami")
    public Mono<Map<String, String>> whoami(ServerWebExchange exchange) {
        return Mono.just(Map.of(
                "x-user-id",    String.valueOf(exchange.getRequest().getHeaders().getFirst("x-user-id")),
                "x-user-roles", String.valueOf(exchange.getRequest().getHeaders().getFirst("x-user-roles"))));
    }

    @GetMapping("/{id}")
    public Mono<Lead> get(@PathVariable UUID id) {
        return leads.findOwned(id);
    }

    @PostMapping
    public Mono<LeadsResponse> create(@RequestBody CreateLeadRequest newLead) {
        return leads.create(newLead)
                .flatMap(lead -> comments.getCommentsByLeadId(lead.getId())
                        .map(commentsList -> new LeadsResponse(
                                lead.getName(),
                                lead.getCompany(),
                                lead.getEmail(),
                                lead.getStage(),
                                String.valueOf(lead.getOwner_id()),
                                String.valueOf(lead.getCreatedAt()),
                                commentsList
                        )));
    }
}
