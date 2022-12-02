package com.vmsac.vmsacserver.repository;


import com.vmsac.vmsacserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    List<User> findByRoles_IdOrderByIdAsc(Integer id);

}
