package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.exception.TokenRefreshException;
import com.vmsac.vmsacserver.model.ERole;
import com.vmsac.vmsacserver.model.RefreshToken;
import com.vmsac.vmsacserver.model.Role;
import com.vmsac.vmsacserver.model.User;
import com.vmsac.vmsacserver.payload.request.LoginRequest;
import com.vmsac.vmsacserver.payload.request.SignupRequest;
import com.vmsac.vmsacserver.payload.request.TokenRefreshRequest;
import com.vmsac.vmsacserver.payload.response.JwtResponse;
import com.vmsac.vmsacserver.payload.response.MessageResponse;
import com.vmsac.vmsacserver.payload.response.TokenRefreshResponse;
import com.vmsac.vmsacserver.repository.RoleRepository;
import com.vmsac.vmsacserver.repository.UserRepository;
import com.vmsac.vmsacserver.security.jwt.JwtUtils;
import com.vmsac.vmsacserver.security.services.RefreshTokenService;
import com.vmsac.vmsacserver.security.services.UserDetailsImpl;
import com.vmsac.vmsacserver.security.services.UserDetailsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRepository user;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getEmail());

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getEmail(), roles));
    }

    // allow user to view all the lower tier accs
    @GetMapping("/accounts")
    public ResponseEntity<?> listOfAccounts() {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails.getAuthorities().stream().anyMatch(i -> (i.toString().equals("ROLE_SYSTEM_ADMIN")))){
                Map<String,List<UserDetailsList>> newList = new HashMap<>();
                newList.put("User-Admin",UserDetailsList.userToUserDetailsList(userRepository.findByRoles_IdOrderByIdAsc(1)));
                newList.put("Tech-Admin",UserDetailsList.userToUserDetailsList(userRepository.findByRoles_IdOrderByIdAsc(3)));
                return new ResponseEntity<>(newList, HttpStatus.OK);
            }else if (userDetails.getAuthorities().stream().anyMatch(i -> (i.toString().equals("ROLE_TECH_ADMIN")))){
                Map<String,List<UserDetailsList>> newList = new HashMap<>();
                newList.put("User-Admin",UserDetailsList.userToUserDetailsList(userRepository.findByRoles_IdOrderByIdAsc(1)));
                return new ResponseEntity<>(newList, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.OK);
            }


        }
        catch(Exception e) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "User Not Found");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }

    }

//    @PostMapping("/delete")
//    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
//
//        Authentication authentication = authenticationManager
//                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//
//        String jwt = jwtUtils.generateJwtToken(userDetails);
//
//        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
//                .collect(Collectors.toList());
//
//        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getEmail());
//
//        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
//                userDetails.getEmail(), roles));
//    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }


        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userAdminRole = roleRepository.findByName(ERole.ROLE_USER_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userAdminRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "Tech-Admin":
                        Role techAdminRole = roleRepository.findByName(ERole.ROLE_TECH_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(techAdminRole);

                        break;
                    case "System-Admin":
                        Role systemAdminRole = roleRepository.findByName(ERole.ROLE_SYSTEM_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(systemAdminRole);

                        break;
                    default:
                        Role userAdminRole = roleRepository.findByName(ERole.ROLE_USER_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userAdminRole);
                }
            });
        }

        User user = new User(signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), signUpRequest.getFirstName(), signUpRequest.getLastName(), signUpRequest.getMobile());
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        try {
            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ArrayList role_names = new ArrayList<>();
                        for (Role ele : user.getRoles()) {
                            role_names.add(ele.getName().toString());
                        }
                        String token = jwtUtils.generateTokenFromEmail(user.getEmail(), role_names);
                        return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                    })
                    .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                            "Refresh token is not in database!"));
        }
        catch (Exception e) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", e.getMessage());
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = userDetails.getId();
            refreshTokenService.deleteByUserId(userId);
            return ResponseEntity.ok(new MessageResponse("Log out successful!"));
        }
        catch(Exception e) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "Unable to Logout");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return new ResponseEntity<>(userDetails, HttpStatus.OK);

        }
        catch(Exception e) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "User Not Found");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }

    }

}
