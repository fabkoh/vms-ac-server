package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredTypeRepository extends JpaRepository<CredentialType, Long> {
}
