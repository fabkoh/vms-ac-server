package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.credential.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

    Optional<Credential> findByCredTypeCredTypeIdAndCredUidAndDeletedFalse(Long credTypeId, String credUid);

}
