package com.thriveq.crm.external_authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExternalAuthorizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExternalAuthorizationApplication.class, args);
	}

}
