package com.vmsac.vmsacserver;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import com.vmsac.vmsacserver.model.ERole;
import com.vmsac.vmsacserver.model.Role;
import com.vmsac.vmsacserver.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableScheduling
@SpringBootApplication
@Configuration
@EnableEncryptableProperties
public class VmsAcServerApplication {
	@Autowired
	private RoleRepository repository;

	@PostConstruct
	public void initRoles() {
		List<Role> roles = Stream.of(
				new Role(ERole.ROLE_USER_ADMIN),
				new Role(ERole.ROLE_SYSTEM_ADMIN),
				new Role(ERole.ROLE_TECH_ADMIN)
		).collect(Collectors.toList());
		repository.saveAll(roles);
	}
	public static void main(String[] args) {
		SpringApplication.run(VmsAcServerApplication.class, args);
	}

}
