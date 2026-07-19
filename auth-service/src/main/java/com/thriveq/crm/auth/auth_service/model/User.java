package com.thriveq.crm.auth.auth_service.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Table("users")
@Data
public class User {
    private UUID id;
    private String email;

    @Column("password_hash")
    private String passwordHash;

    private boolean active;

    @Column("failed_count")
    private Integer failedCount;

    @Column("locked_until")
    private Timestamp lockedUntil;

    @Column("created_at")
    private Timestamp createdAt;
}
