package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CredTypeRepository extends JpaRepository<CredentialType, Long> {
    List<CredentialType> findAll();
}
