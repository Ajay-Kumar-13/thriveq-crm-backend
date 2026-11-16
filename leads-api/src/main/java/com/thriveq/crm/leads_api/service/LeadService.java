package com.thriveq.crm.leads_api.service;

import com.thriveq.crm.leads_api.model.Lead;
import com.thriveq.crm.leads_api.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository repo;

    public Mono<Lead> findOwned(Long id) {
        return repo.findById(id)          // scope in the QUERY
                .switchIfEmpty(Mono.error(new RuntimeException("Lead not found with id "+ id)));
    }

    public Mono<Lead> create(com.thriveq.crm.leads_api.dto.NewLead newLead) {
        Lead lead = new Lead();
        lead.setName(newLead.getName());
        lead.setCompany(newLead.getCompany());
        lead.setEmail(newLead.getEmail());
        lead.setStage(newLead.getStage());
        lead.setOwner_id(newLead.getOwner_id());

        return repo.save(lead)
            .flatMap(saved -> repo.findById(saved.getId())
                .switchIfEmpty(Mono.just(saved)));
    }
}