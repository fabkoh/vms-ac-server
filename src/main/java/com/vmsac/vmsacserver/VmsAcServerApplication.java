package com.vmsac.vmsacserver;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@Configuration
@EnableEncryptableProperties
public class VmsAcServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VmsAcServerApplication.class, args);
	}

}
