package com.thriveq.crm.auth.auth_service.model;

import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("roles")
public class Role {
    public UUID id;
    public String name;
}
