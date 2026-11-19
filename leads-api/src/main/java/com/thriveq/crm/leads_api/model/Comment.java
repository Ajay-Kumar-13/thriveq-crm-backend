package com.thriveq.crm.leads_api.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("comments")
@Data 
public class Comment {
    @Id 
    private UUID id;

    @Column("lead_id")
    private UUID leadId;

    @Column("author_id")
    private UUID authorId;

    private String content;

    @Column("created_at")
    private String createdAt;

}
