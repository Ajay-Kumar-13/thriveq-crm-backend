package com.thriveq.crm.leads_api.service;

import org.springframework.stereotype.Service;

import com.thriveq.crm.leads_api.dto.LeadEvent;
import com.thriveq.crm.leads_api.model.LeadActivity;
import com.thriveq.crm.leads_api.repository.LeadActivityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service 
@RequiredArgsConstructor 
public class LeadActivityService {
    private final LeadActivityRepository leadActivityRepository;

    public Mono<Void> saveLeadActivity(LeadEvent leadEvent) {
        LeadActivity leadActivity = new LeadActivity();
        leadActivity.setEventId(leadEvent.eventId());
        leadActivity.setLeadId(leadEvent.leadId());
        leadActivity.setAuthorId(leadEvent.data().ownerId());
        leadActivity.setChanges(leadEvent.changes());
        leadActivity.setOccurredAt(leadEvent.timestamp());
        
        return leadActivityRepository.save(leadActivity).then();
    }
}
