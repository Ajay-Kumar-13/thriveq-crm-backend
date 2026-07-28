package com.thriveq.crm.leads_api.repository;

import com.thriveq.crm.leads_api.model.Lead;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface LeadRepository extends R2dbcRepository<Lead, UUID> {
    Mono<Lead> findByIdAndOwnerId(UUID id, UUID ownerId);
}