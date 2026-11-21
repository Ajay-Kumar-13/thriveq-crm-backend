package com.thriveq.crm.leads_api.service;

import com.thriveq.crm.leads_api.dto.CreateLeadRequest;
import com.thriveq.crm.leads_api.dto.LeadsResponse;
import com.thriveq.crm.leads_api.model.Lead;
import com.thriveq.crm.leads_api.repository.LeadRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leads;
    private final CommentService comments;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public Mono<LeadsResponse> findLeadById(UUID id) {
        kafkaTemplate.send("lead-events", "Lead with ID " + id + " was accessed.");
        return leads.findById(id)         
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

        return leads.save(lead)
            .flatMap(saved -> findLeadById(saved.getId()));
    }

    public Mono<LeadsResponse> updateLead(UUID id, CreateLeadRequest updatedLead) {
        return leads.findById(id)
            .flatMap(existingLead -> {
                existingLead.setName(updatedLead.name());
                existingLead.setCompany(updatedLead.company());
                existingLead.setEmail(updatedLead.email());
                existingLead.setStage(updatedLead.stage());
                existingLead.setOwner_id(updatedLead.owner_id());
                return leads.save(existingLead);
            })
            .flatMap(saved -> findLeadById(saved.getId()));
    }

    @KafkaListener (topics = "lead-events", groupId = "leads-activity")
    public void handleLeadEvent(String leadEvent) {
        // Handle the lead event here
        System.out.println("Received lead event: " + leadEvent);
    }
}