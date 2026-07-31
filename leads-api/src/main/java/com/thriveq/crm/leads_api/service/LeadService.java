package com.thriveq.crm.leads_api.service;

import com.thriveq.crm.leads_api.model.Lead;
import com.thriveq.crm.leads_api.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository repo;

    public Mono<Lead> findOwned(UUID id) {
        return repo.findById(id)          // scope in the QUERY
                .switchIfEmpty(Mono.error(new RuntimeException("Lead not found with id "+ id)));
    }
}