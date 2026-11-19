package com.thriveq.crm.leads_api.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.thriveq.crm.leads_api.model.Comment;

import reactor.core.publisher.Flux;

@Repository 
public interface CommentRepository extends R2dbcRepository<Comment, UUID> {
        Flux<Comment> findByLeadId(UUID leadId);
}
