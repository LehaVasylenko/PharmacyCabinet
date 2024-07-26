package com.orders.cabinet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableAsync
public class CabinetApplication {

	public static void main(String[] args) {
		SpringApplication.run(CabinetApplication.class, args);
	}

}
