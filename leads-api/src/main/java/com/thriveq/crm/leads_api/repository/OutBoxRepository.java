package com.thriveq.crm.leads_api.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.thriveq.crm.leads_api.model.Outbox;

@Repository 
public interface OutBoxRepository extends R2dbcRepository<Outbox, UUID>{
    
}
