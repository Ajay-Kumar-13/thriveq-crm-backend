package com.thriveq.crm.external_authorization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionCatalog {
    private final DatabaseClient db;

    public Set<String> allNames() {
        return db.sql("SELECT name from auth.permissions")
                .map(r -> r.get("name", String.class))
                .all()
                .collect(Collectors.toSet())
                .block();
    }
}
