package com.thriveq.crm.auth.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TokenResponse {
    public String accessToken;
    public String type;
    public long ttl;
}
