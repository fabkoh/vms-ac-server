package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthMethodRepository extends JpaRepository<AuthMethod, Long> {
}
