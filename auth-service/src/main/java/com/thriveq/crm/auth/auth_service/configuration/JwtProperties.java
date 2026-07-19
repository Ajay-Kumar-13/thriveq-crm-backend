package com.thriveq.crm.auth.auth_service.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    public String privateKeyPath;
    public String kid;
    public String audience;
    public long ttlSeconds;
    public String issuer;
}
