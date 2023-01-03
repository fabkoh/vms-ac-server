package com.vmsac.vmsacserver;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import com.vmsac.vmsacserver.model.ERole;
import com.vmsac.vmsacserver.model.Role;
import com.vmsac.vmsacserver.model.User;
import com.vmsac.vmsacserver.repository.RoleRepository;
import com.vmsac.vmsacserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableScheduling
@SpringBootApplication
@Configuration
@EnableEncryptableProperties
public class VmsAcServerApplication {
	@Autowired
	private RoleRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@PostConstruct
	public void initRoles() {
		List<Role> roles = Stream.of(
				new Role(ERole.ROLE_USER_ADMIN),
				new Role(ERole.ROLE_SYSTEM_ADMIN),
				new Role(ERole.ROLE_TECH_ADMIN)
		).collect(Collectors.toList());
		repository.saveAllAndFlush(roles);

		Set<Role> chosen = new HashSet<>();
		chosen.add(repository.findByName(ERole.ROLE_SYSTEM_ADMIN).get());
		User user = new User();
		user.setFirstName("ISS");
		user.setLastName("Admin");
		user.setEmail("ISSAdmin@isssecurity.sg");
		user.setPassword(encoder.encode("ISSAdmin"));
		user.setRoles(chosen);
		user.setDeleted(false);
		user.setMobile("12345678");
		userRepository.save(user);
	}


	public static void main(String[] args) {
		SpringApplication.run(VmsAcServerApplication.class, args);
	}

}
