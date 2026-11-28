package com.thriveq.crm.leads_api.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.thriveq.crm.leads_api.constants.Events;
import com.thriveq.crm.leads_api.dto.LeadEvent;
import com.thriveq.crm.leads_api.service.LeadActivityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@Slf4j 
@RequiredArgsConstructor 
public class LeadConsumer {

    private final LeadActivityService leadActivityService;

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
