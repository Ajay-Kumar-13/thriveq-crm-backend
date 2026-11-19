package com.thriveq.crm.leads_api.repository;

import com.thriveq.crm.leads_api.model.Lead;


import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends R2dbcRepository<Lead, UUID> {

}