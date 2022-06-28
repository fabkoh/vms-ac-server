package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.credential.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

    List<Credential> findByDeleted(Boolean deleted);

    Optional<Credential> findByCredTypeCredTypeIdAndCredUidAndDeletedFalse(Long credTypeId, String credUid);

    List<Credential> findAllByPersonPersonIdAndDeletedFalse(Long personId);

    Optional<Credential> findByCredIdAndDeletedFalse(Long credId);

    List<Credential> findAllByDeletedFalse();
}
