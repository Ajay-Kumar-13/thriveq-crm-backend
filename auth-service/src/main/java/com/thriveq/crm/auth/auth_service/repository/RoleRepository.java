package com.thriveq.crm.auth.auth_service.repository;

import com.thriveq.crm.auth.auth_service.model.Role;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface RoleRepository extends R2dbcRepository<Role, UUID> {
    @Query("""
        SELECT r.name FROM auth.roles r
        JOIN auth.user_roles ur ON ur.role_id = r.id
        WHERE ur.user_id = :userId
        """)
    Flux<String> findRoleNamesByUserId(UUID userId);
}
