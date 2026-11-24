package com.thriveq.crm.leads_api.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.thriveq.crm.leads_api.model.LeadActivity;

@Repository 
public interface LeadActivityRepository extends R2dbcRepository<LeadActivity, UUID> {

}
