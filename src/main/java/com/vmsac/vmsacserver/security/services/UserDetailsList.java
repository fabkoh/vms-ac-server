package com.vmsac.vmsacserver.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmsac.vmsacserver.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsList implements UserDetails {
        private static final long serialVersionUID = 1L;

        private Long id;

        private String firstName;

        private String lastName;

        private String email;

        private String mobile;

        @JsonIgnore
        private String password;

        private Collection<? extends GrantedAuthority> authorities;

        public UserDetailsList(Long id, String email, String password, String firstName, String lastName, String mobile) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.lastName = lastName;
            this.firstName = firstName;
            this.mobile = mobile;
        }

        public static List<UserDetailsList> userToUserDetailsList(List<User> lsUser){
            return lsUser.stream().map(user ->
                    new UserDetailsList(
                            user.getId(),
                            user.getEmail(),
                            user.getPassword(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getMobile()
                    )).collect(Collectors.toList());
        }


        public Long getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() { return firstName; }

        public String getLastName() { return lastName; }

        public String getMobile() { return mobile; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            UserDetailsList user = (UserDetailsList) o;
            return Objects.equals(id, user.id);
        }
    }

