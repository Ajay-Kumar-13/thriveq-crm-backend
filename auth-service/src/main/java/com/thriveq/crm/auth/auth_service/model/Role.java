package com.thriveq.crm.auth.auth_service.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("roles")
@Data

public class Role {
    private UUID id;
    private String name;
}
