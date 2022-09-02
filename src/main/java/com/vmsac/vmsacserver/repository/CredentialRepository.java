package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.credential.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

    List<Credential> findByDeleted(Boolean deleted);

    Optional<Credential> findByDeletedFalseAndCredUidAndCredType_CredTypeIdNotAndCredIdNot(String credUid, Long credTypeId, Long credId);


    Optional<Credential> findByCredTypeCredTypeIdAndCredUidAndDeletedFalse(Long credTypeId, String credUid);

    Optional<Credential> findByDeletedFalseAndCredType_CredTypeIdAndCredUidAndPerson_PersonId(Long credTypeId, String credUid, Long personId);

    List<Credential> findAllByPersonPersonIdAndDeletedFalse(Long personId);

    Optional<Credential> findByCredIdAndDeletedFalse(Long credId);

    List<Credential> findAllByDeletedFalse();

    List<Credential> findByCredUidContainsAndDeletedFalse(String credUid);

    List<Credential> findByCredUidContainsAndDeletedFalseAndPerson_PersonId(String credUid, Long personId);

}
