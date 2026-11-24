package com.thriveq.crm.leads_api.model;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("lead_activities")
@Data 
public class LeadActivity {
    @Id 
    private UUID id;

    @Column("event_id")
    private UUID eventId;

    @Column("lead_id")
    private UUID leadId;

    @Column("author_id")
    private UUID authorId;

    @Column("changes")
    private Object changes;

    @Column("occurred_at")
    private Instant occurredAt;
}
