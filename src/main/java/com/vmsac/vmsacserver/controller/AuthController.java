package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.exception.TokenRefreshException;
import com.vmsac.vmsacserver.model.*;
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
import com.vmsac.vmsacserver.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    UserDetailsServiceImpl userDetailsService;

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
        System.out.println("Sign in start");
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getEmail());

        if (userRepository.findById(userDetails.getId()).isPresent()){
            if(userRepository.findById(userDetails.getId()).get().getDeleted()){
                Map<String, String> map = new HashMap<String, String>();
                map.put("message", "User Not Found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }
        }

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
                newList.put("User-Admin",UserDetailsList.userToUserDetailsList(userRepository.findByDeletedFalseAndRoles_IdOrderByIdAsc(1)));
                newList.put("Tech-Admin",UserDetailsList.userToUserDetailsList(userRepository.findByDeletedFalseAndRoles_IdOrderByIdAsc(3)));
                return new ResponseEntity<>(newList, HttpStatus.OK);
            }else if (userDetails.getAuthorities().stream().anyMatch(i -> (i.toString().equals("ROLE_TECH_ADMIN")))){
                Map<String,List<UserDetailsList>> newList = new HashMap<>();
                newList.put("User-Admin",UserDetailsList.userToUserDetailsList(userRepository.findByDeletedFalseAndRoles_IdOrderByIdAsc(1)));
                return new ResponseEntity<>(newList, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        catch(Exception e) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "User Not Found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
    }

    // dummy password
    @PutMapping("/edit")
    public ResponseEntity<?> editUser(@Valid @RequestBody User user) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = userDetails.getId();
            User user_to_edit = userRepository.findById(userId).get();
            if (!userRepository.findByDeletedFalseAndEmail(user.getEmail()).isEmpty() &&
                    !(userRepository.findByDeletedFalseAndEmail(user.getEmail()).get().getId().equals(userId))) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
            }
            if (!userDetails.getId().equals(userId)){
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Not allowed to edit user"));
            }
            user_to_edit.setEmail(user.getEmail());
            user_to_edit.setMobile(user.getMobile());
            user_to_edit.setFirstName(user.getFirstName());
            user_to_edit.setLastName(user.getLastName());
            userRepository.saveAndFlush(user_to_edit);


            UserDetailsImpl userDetailsReturn = (UserDetailsImpl) userDetailsService.loadUserByUsername(
                    user_to_edit.getEmail());
            String jwt = jwtUtils.generateJwtToken(userDetailsReturn);

            List<String> roles = userDetailsReturn.getAuthorities().stream().map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetailsReturn.getEmail());

            return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetailsReturn.getId(),
                    userDetailsReturn.getEmail(), roles));

        }catch(Exception e) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "User Not Found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        //dsfsdf get password instead of loginreq, allow user to edit all, create one more for chagning password

    }

    @PutMapping("/changingPassword")
    public ResponseEntity<?> changingPassword(@Valid @RequestBody UserPassword userPassword) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = userDetails.getId();
            User user_to_edit = userRepository.findById(userId).get();
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user_to_edit.getEmail(), userPassword.getOldpassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            user_to_edit.setPassword(encoder.encode(userPassword.getNewpassword()));
            userRepository.saveAndFlush(user_to_edit);

            UserDetailsImpl userDetailsReturn = (UserDetailsImpl) userDetailsService.loadUserByUsername(
                    user_to_edit.getEmail());
            String jwt = jwtUtils.generateJwtToken(userDetailsReturn);

            List<String> roles = userDetailsReturn.getAuthorities().stream().map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetailsReturn.getEmail());

            return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetailsReturn.getId(),
                    userDetailsReturn.getEmail(), roles));

        }catch(Exception e) {
            System.out.println(e);
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "User Not Found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        //dsfsdf get password instead of loginreq, allow user to edit all, create one more for chagning password

    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_TECH_ADMIN')")
    @DeleteMapping("/deleteUserAdmin/{id}")
    public ResponseEntity<?> deleteUserAdmin(@PathVariable Long id) {

        try {
            User user = userRepository.findById(id).get();
            System.out.println(user.getEmail());
            if (!user.getRoles().contains(roleRepository.findByRoleName(ERole.ROLE_USER_ADMIN).get())) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("message", "User Not Found");
                return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
            }
            refreshTokenService.deleteByUserId(user.getId());
            user.setDeleted(true);
            userRepository.save(user);
            System.out.println("deleted");
            return new ResponseEntity<>(HttpStatus.OK);

        }
        catch(Exception e) {
            System.out.println(e.toString());
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "User Not Found");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }
    }

    // check if still logged in, if so, return
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @DeleteMapping("/deleteTechAdmin/{id}")
    public ResponseEntity<?> deleteTechAdmin(@PathVariable Long id) {

        try {
            User user = userRepository.findById(id).get();
            if (!(user.getRoles().contains(roleRepository.findByRoleName(ERole.ROLE_USER_ADMIN).get()) ||
                    user.getRoles().contains(roleRepository.findByRoleName(ERole.ROLE_TECH_ADMIN).get()))) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("message", "User Not Found");
                return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
            }
            refreshTokenService.deleteByUserId(user.getId());
            user.setDeleted(true);
            userRepository.save(user);
            System.out.println("deleted");
            return new ResponseEntity<>(HttpStatus.OK);

        }
        catch(Exception e) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "User Not Found");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByDeletedFalseAndEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }


        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userAdminRole = roleRepository.findByRoleName(ERole.ROLE_USER_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userAdminRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "Tech-Admin":
                        Role techAdminRole = roleRepository.findByRoleName(ERole.ROLE_TECH_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(techAdminRole);

                        break;
                    case "System-Admin":
                        Role systemAdminRole = roleRepository.findByRoleName(ERole.ROLE_SYSTEM_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(systemAdminRole);

                        break;
                    default:
                        Role userAdminRole = roleRepository.findByRoleName(ERole.ROLE_USER_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userAdminRole);
                }
            });
        }

        User user = new User(signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), signUpRequest.getFirstName(), signUpRequest.getLastName(), signUpRequest.getMobile(),false);
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
                            role_names.add(ele.getRoleName().toString());
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
