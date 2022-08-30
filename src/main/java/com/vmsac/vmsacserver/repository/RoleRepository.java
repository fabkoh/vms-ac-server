package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.ERole;
import com.vmsac.vmsacserver.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
