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

        if (repository.findAll().isEmpty()) {
//            System.out.println("123458");
            List<Role> roles = Stream.of(
                    new Role(ERole.ROLE_USER_ADMIN),
                    new Role(ERole.ROLE_SYSTEM_ADMIN),
                    new Role(ERole.ROLE_TECH_ADMIN)
            ).collect(Collectors.toList());
            repository.saveAllAndFlush(roles);
//            System.out.println("123459");
            
            Set<Role> system = new HashSet<>();
            system.add(repository.findByRoleName(ERole.ROLE_SYSTEM_ADMIN).get());
            User System = new User();
            System.setFirstName("System");
            System.setLastName("Admin");
            System.setEmail("ISSAdmin@isssecurity.sg");
            System.setPassword(encoder.encode("ISSAdmin"));
            System.setRoles(system);
            System.setDeleted(false);
            System.setMobile("12345678");
            userRepository.save(System);

            Set<Role> tech = new HashSet<>();
            tech.add(repository.findByRoleName(ERole.ROLE_TECH_ADMIN).get());
            User techUser = new User();
            techUser.setFirstName("Tech");
            techUser.setLastName("Admin");
            techUser.setEmail("TechUser@isssecurity.sg");
            techUser.setPassword(encoder.encode("ISSAdmin"));
            techUser.setRoles(tech);
            techUser.setDeleted(false);
            techUser.setMobile("1234567");
            userRepository.save(techUser);

            Set<Role> user = new HashSet<>();
            user.add(repository.findByRoleName(ERole.ROLE_USER_ADMIN).get());
            User user1 = new User();
            user1.setFirstName("user1");
            user1.setLastName("Admin");
            user1.setEmail("User@isssecurity.sg");
            user1.setPassword(encoder.encode("ISSAdmin"));
            user1.setRoles(user);
            user1.setDeleted(false);
            user1.setMobile("123456789");
            userRepository.save(user1);

        }
    }

    public static void main(String[] args) {
        SpringApplication.run(VmsAcServerApplication.class, args);
    }

}
