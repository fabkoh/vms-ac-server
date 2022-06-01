package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.authmethodcredentialtypenton.AuthMethodCredentialTypeNtoN;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthMethodCredentialTypeNtoNRepository extends JpaRepository<AuthMethodCredentialTypeNtoN, Long> {
}
