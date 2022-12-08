package com.vmsac.vmsacserver.repository;


import com.vmsac.vmsacserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByDeletedFalseAndEmail(String email);

    boolean existsByDeletedFalseAndEmail(String email);

    List<User> findByDeletedFalseAndRoles_IdOrderByIdAsc(Integer id);





}
