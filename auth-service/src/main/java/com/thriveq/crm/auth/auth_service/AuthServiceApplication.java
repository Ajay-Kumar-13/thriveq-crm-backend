package com.thriveq.crm.auth.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;

@SpringBootApplication
/**
 * Validated at startup, autocompletes in the IDE,
 * and if a required value is missing you find out when the app boots rather than when a token fails to sign.
 * **/
@ConfigurationProperties
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
