package com.thriveq.crm.auth.auth_service.repository;

import com.thriveq.crm.auth.auth_service.model.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends R2dbcRepository<User, UUID> {
    Mono<User> findByEmail(String email);
    @Modifying
    @Query("""
        UPDATE auth.users
        SET failedCount = failedCount + 1,
            lockedUntil = CASE WHEN failedCount + 1 >= 5
                                THEN now() + interval '15 minutes'
                                ELSE lockedUntil END
        WHERE id = :id
        """)
    Mono<Void> recordFailure(UUID id);

    @Modifying
    @Query("UPDATE auth.users SET failedCount = 0, lockedUntil = NULL WHERE id = :id")
    Mono<Void> resetFailures(UUID id);
}
