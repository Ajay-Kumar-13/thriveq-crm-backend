package com.thriveq.crm.leads_api.model;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("outbox")
@Data 
public class Outbox {
    @Id 
    private UUID id;

    private Object payload;

    private UUID aggregateid;

    private String aggregatetype;

    @Column("created_at")
    private Instant created_at;
}
