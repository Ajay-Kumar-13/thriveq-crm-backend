package com.thriveq.crm.external_authortization.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

@Configuration
public class RbacPolicyLoader {

    @Bean
    public RbacPolicy rbacPolicy() throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory());
        //  Java's try-with-resources guarantees it gets closed automatically when the block ends.
        try (var in = new ClassPathResource("rbac-policy.yaml").getInputStream()) {
            return mapper.readValue(in, RbacPolicy.class);
        }
    }
}
