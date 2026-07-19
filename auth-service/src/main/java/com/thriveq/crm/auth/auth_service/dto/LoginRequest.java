package com.thriveq.crm.auth.auth_service.dto;

import lombok.Data;

@Data
public class LoginRequest {
    public String email;
    public boolean active;
    public String password;
}
