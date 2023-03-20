package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.User;
import com.vmsac.vmsacserver.repository.*;
import com.vmsac.vmsacserver.security.jwt.JwtUtils;
import com.vmsac.vmsacserver.security.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;


    public User editUserProfile(User newchanges){
        User currentUserProfile = userRepository.findAll().get(0);
        currentUserProfile.setFirstName(newchanges.getFirstName());
        currentUserProfile.setLastName(newchanges.getLastName());
        currentUserProfile.setEmail(newchanges.getEmail());
        currentUserProfile.setMobile(newchanges.getMobile());
        currentUserProfile.setPassword(newchanges.getPassword());

        return null;
    }

}
