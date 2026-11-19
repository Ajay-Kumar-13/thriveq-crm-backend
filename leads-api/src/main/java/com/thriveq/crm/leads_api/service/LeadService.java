package com.thriveq.crm.leads_api.service;

import com.thriveq.crm.leads_api.dto.CreateLeadRequest;
import com.thriveq.crm.leads_api.model.Lead;
import com.thriveq.crm.leads_api.repository.LeadRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository repo;

    public Mono<Lead> findOwned(UUID id) {
        return repo.findById(id)          // scope in the QUERY
                .switchIfEmpty(Mono.error(new RuntimeException("Lead not found with id "+ id)));
    }

    public Mono<Lead> create(CreateLeadRequest newLead) {
        Lead lead = new Lead();
        lead.setName(newLead.name());
        lead.setCompany(newLead.company());
        lead.setEmail(newLead.email());
        lead.setStage(newLead.stage());
        lead.setOwner_id(newLead.owner_id());

        return repo.save(lead)
            .flatMap(saved -> repo.findById(saved.getId())
                .switchIfEmpty(Mono.just(saved)));
    }
}