package com.thriveq.crm.leads_api.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record LeadEvent(
    UUID eventId,               // Unique ID for this event (prevents duplicate processing)
    String eventType,           // LEAD_CREATED, LEAD_UPDATED, LEAD_STAGE_CHANGED
    Instant timestamp,          // When the event happened
    UUID leadId,                // The lead's ID
    LeadSnapshot data,          // Full current state of the lead
    Map<String, FieldChange> changes // Only populated for updates (old -> new)
) {
    public record LeadSnapshot(
        String name,
        String company,
        String email,
        String stage,
        UUID ownerId
    ) {}

    public record FieldChange(
        Object oldValue,
        Object newValue
    ) {}
}
