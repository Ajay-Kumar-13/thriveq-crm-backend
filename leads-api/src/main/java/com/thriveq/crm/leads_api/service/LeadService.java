package com.thriveq.crm.leads_api.service;

import com.thriveq.crm.leads_api.dto.CreateLeadRequest;
import com.thriveq.crm.leads_api.dto.LeadsResponse;
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
    private final CommentService comments;

    public Mono<LeadsResponse> findLeadById(UUID id) {
        return repo.findById(id)         
            .flatMap(lead -> comments.getCommentsByLeadId(lead.getId())
            .map(commentsList -> new LeadsResponse(
                    lead.getId(),
                    lead.getName(),
                    lead.getCompany(),
                    lead.getEmail(),
                    lead.getStage(),
                    String.valueOf(lead.getOwner_id()),
                    String.valueOf(lead.getCreatedAt()),
                    commentsList
            )));
    }

    public Mono<LeadsResponse> create(CreateLeadRequest newLead) {
        Lead lead = new Lead();
        lead.setName(newLead.name());
        lead.setCompany(newLead.company());
        lead.setEmail(newLead.email());
        lead.setStage(newLead.stage());
        lead.setOwner_id(newLead.owner_id());

        return repo.save(lead)
            .flatMap(saved -> repo.findById(saved.getId()))
            .map(l -> new LeadsResponse(
                    l.getId(),
                    l.getName(),
                    l.getCompany(),
                    l.getEmail(),
                    l.getStage(),
                    String.valueOf(l.getOwner_id()),
                    String.valueOf(l.getCreatedAt()),
                    null
            ));
    }
}