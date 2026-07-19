package com.thriveq.crm.auth.auth_service.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Table("users")
@Data
public class User {
    public UUID id;
    public String email;
    public String password_hash;
    public boolean active;
    public Integer failed_count;
    public Timestamp locked_until;
    public Timestamp created_at;
}
