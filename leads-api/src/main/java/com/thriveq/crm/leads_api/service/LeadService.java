package com.thriveq.crm.leads_api.service;

import com.thriveq.crm.leads_api.constants.Events;
import com.thriveq.crm.leads_api.dto.CreateLeadRequest;
import com.thriveq.crm.leads_api.dto.LeadEvent;
import com.thriveq.crm.leads_api.dto.LeadsResponse;
import com.thriveq.crm.leads_api.model.Lead;
import com.thriveq.crm.leads_api.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j 
public class LeadService {

    private final LeadRepository leads;
    private final CommentService comments;
    private final LeadActivityService leadActivityService;
    private final KafkaTemplate<String, LeadEvent> kafkaTemplate;

    public Mono<LeadsResponse> findLeadById(UUID id) {
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
            .doOnSuccess(savedLead -> {
                LeadEvent leadEvent = new LeadEvent(
                    UUID.randomUUID(),
                    Events.LEAD_CREATED,
                    java.time.Instant.now(),
                    savedLead.getId(),
                    new LeadEvent.LeadSnapshot(
                        savedLead.getName(),
                        savedLead.getCompany(),
                        savedLead.getEmail(),
                        savedLead.getStage(),
                        savedLead.getOwner_id()
                    ),
                    null // No changes for creation
                );
                kafkaTemplate.send("lead-events", leadEvent);
            })
            .flatMap(saved -> findLeadById(saved.getId()));
    }

    public Mono<LeadsResponse> updateLead(UUID id, CreateLeadRequest updatedLead) {
        return leads.findById(id)
            .flatMap(existingLead -> {
                Map<String, LeadEvent.FieldChange> changes = calculateChanges(existingLead, updatedLead);
                existingLead.setName(updatedLead.name());
                existingLead.setCompany(updatedLead.company());
                existingLead.setEmail(updatedLead.email());
                existingLead.setStage(updatedLead.stage());
                existingLead.setOwner_id(updatedLead.owner_id());
                return Mono.just(changes).zipWith(leads.save(existingLead));
            })
            .doOnSuccess(zip -> {
                Map<String, LeadEvent.FieldChange> changes = zip.getT1();
                Lead savedLead = zip.getT2();

                LeadEvent leadEvent = new LeadEvent(
                    UUID.randomUUID(),
                    Events.LEAD_UPDATED,
                    java.time.Instant.now(),
                    savedLead.getId(),
                    new LeadEvent.LeadSnapshot(
                        savedLead.getName(),
                        savedLead.getCompany(),
                        savedLead.getEmail(),
                        savedLead.getStage(),
                        savedLead.getOwner_id()
                    ),
                    changes 
                );
                kafkaTemplate.send("lead-events", leadEvent);
            })
            .flatMap(zip -> findLeadById(zip.getT2().getId()));
    }

    private Map<String, LeadEvent.FieldChange> calculateChanges(Lead existingLead, CreateLeadRequest updatedLead) {
        Map<String, LeadEvent.FieldChange> changes = new java.util.HashMap<>();
        if (!existingLead.getName().equals(updatedLead.name())) {
            changes.put("name", new LeadEvent.FieldChange(existingLead.getName(), updatedLead.name()));
        }
        if (!existingLead.getCompany().equals(updatedLead.company())) {
            changes.put("company", new LeadEvent.FieldChange(existingLead.getCompany(), updatedLead.company()));
        }
        if (!existingLead.getEmail().equals(updatedLead.email())) {
            changes.put("email", new LeadEvent.FieldChange(existingLead.getEmail(), updatedLead.email()));
        }
        if (!existingLead.getStage().equals(updatedLead.stage())) { 
            changes.put("stage", new LeadEvent.FieldChange(existingLead.getStage(), updatedLead.stage()));
        }
        if (!existingLead.getOwner_id().equals(updatedLead.owner_id())) {
            changes.put("owner_id", new LeadEvent.FieldChange(existingLead.getOwner_id(), updatedLead.owner_id()));
        }
        return changes;
    }

    @KafkaListener (topics = "lead-events", groupId = "leads-activity")
    public void handleLeadEvent(LeadEvent leadEvent) {
        switch (leadEvent.eventType()) {
            case Events.LEAD_CREATED:
                log.info("Lead created: {}", leadEvent.leadId());
                break;
            case Events.LEAD_UPDATED:
                log.info("Lead updated: {}", leadEvent.leadId());
                leadActivityService.saveLeadActivity(leadEvent);
                break;
            case Events.LEAD_STAGE_CHANGED:
                // Handle lead stage change event
                break;
            case Events.LEAD_DELETED:
                // Handle lead deletion event
                break;
            default:
                // Handle unknown event type
                break;
        }
    }
}