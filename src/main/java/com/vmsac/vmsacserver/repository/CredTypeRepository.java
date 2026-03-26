package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CredTypeRepository extends JpaRepository<CredentialType, Long> {
    List<CredentialType> findAll();

    Optional<CredentialType> findByCredTypeNameAndDeletedFalse(String credTypeName);
}
