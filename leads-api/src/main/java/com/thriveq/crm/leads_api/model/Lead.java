package com.thriveq.crm.leads_api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("leads")
@Data
public class Lead {
    @Id
    private Long id;

    private String name;
    private String company;
    private String email;
    private String stage;

    @Column("owner_id")
    private UUID owner_id;

    @Column("created_at")
    private Instant createdAt;
}
