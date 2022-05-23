package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthMethodRepository extends JpaRepository<AuthMethod, Long> {

//    List<AuthMethod> findAllByAuthMethodIdAndDeletedFalse(List<Long> authMethodIds);
}
