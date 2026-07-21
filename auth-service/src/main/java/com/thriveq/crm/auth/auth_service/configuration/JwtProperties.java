package com.thriveq.crm.auth.auth_service.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String privateKeyPath;
    private String kid;
    private String audience;
    private long ttlSeconds;
    private String issuer;
}
