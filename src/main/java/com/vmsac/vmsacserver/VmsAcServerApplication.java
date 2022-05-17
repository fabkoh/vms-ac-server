package com.vmsac.vmsacserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VmsAcServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VmsAcServerApplication.class, args);
	}

}
