package com.thriveq.crm.auth.auth_service.controller;

import com.thriveq.crm.auth.auth_service.configuration.JwtProperties;
import com.thriveq.crm.auth.auth_service.dto.LoginRequest;
import com.thriveq.crm.auth.auth_service.dto.TokenResponse;
import com.thriveq.crm.auth.auth_service.model.User;
import com.thriveq.crm.auth.auth_service.repository.RoleRepository;
import com.thriveq.crm.auth.auth_service.repository.UserRepository;
import com.thriveq.crm.auth.auth_service.util.JwtIssuer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final JwtIssuer issuer;
    private final JwtProperties props;

    @GetMapping("/health")
    public Mono<String> health() { return Mono.just("ok"); }

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@Valid @RequestBody LoginRequest req) {
        return users.findByEmail(req.getEmail().toLowerCase())
                .flatMap(user -> {
                    if (!user.isActive() || isLocked(user))
                        return Mono.just(deny());

                    return Mono.fromCallable(() -> encoder.matches(req.getPassword(), user.getPasswordHash()))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(matches -> {
                                if (!matches)
                                    return users.recordFailure(user.getId()).thenReturn(deny());
                                return users.resetFailures(user.getId())
                                        .then(roles.findRoleNamesByUserId(user.getId()).collectList())
                                        .map(roleNames -> ResponseEntity.ok(new TokenResponse(
                                                issuer.issue(user.getId(), user.getEmail(), roleNames),
                                                "Bearer", props.getTtlSeconds())));
                            });
                })
                .defaultIfEmpty(deny())
                // no user found: still hash, to keep timing flat
                .switchIfEmpty(Mono.fromCallable(() -> {
                    encoder.encode(req.getPassword());
                    return deny();
                }).subscribeOn(Schedulers.boundedElastic())
                );
    }

    private ResponseEntity<?> deny() {
        return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
    }

    private boolean isLocked(User u) {
        return u.getLockedUntil() != null && u.getLockedUntil().isAfter(Instant.now());
    }
}