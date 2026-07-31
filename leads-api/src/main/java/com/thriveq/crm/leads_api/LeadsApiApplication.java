package com.thriveq.crm.leads_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = ReactiveUserDetailsServiceAutoConfiguration.class)
public class LeadsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeadsApiApplication.class, args);
	}

}
