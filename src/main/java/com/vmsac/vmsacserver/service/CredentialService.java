package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.credential.CreateCredentialDto;
import com.vmsac.vmsacserver.model.credential.Credential;
import com.vmsac.vmsacserver.model.credential.CredentialDto;
import com.vmsac.vmsacserver.model.credential.EditCredentialDto;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import com.vmsac.vmsacserver.repository.CredTypeRepository;
import com.vmsac.vmsacserver.repository.CredentialRepository;
import com.vmsac.vmsacserver.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CredentialService {

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CredTypeRepository credTypeRepository;

    public List<CredentialDto> findAllNotDeleted() {
        return credentialRepository
                .findAllByDeletedFalse()
                .stream()
                .map(Credential::toDto)
                .collect(Collectors.toList());
    }

    public CredentialDto createCredential(CreateCredentialDto createCred) throws Exception {
        Long credTypeId = createCred.getCredTypeId();

        Optional<Credential> credentialExactCopyFromSamePerson = credentialRepository
                .findByDeletedFalseAndCredType_CredTypeIdAndCredUidAndPerson_PersonId(createCred.getCredTypeId(), createCred.getCredUid(), createCred.getPersonId());

        if (credentialExactCopyFromSamePerson.isPresent()) {
            throw new RuntimeException("Credential type and value repeated from the same person");
        }

        if (credTypeId == null) throw new RuntimeException("Cred type missing");

        CredentialType credType = credTypeRepository.findById(credTypeId)
                .orElseThrow(() -> new RuntimeException("Cred type does not exist"));

        // Check for duplicate exact pairing cred type and cred value iff the cred type is not pin
        if (createCred.getCredTypeId() != 4) {
            if (credentialRepository
                    .findByCredTypeCredTypeIdAndCredUidAndDeletedFalse(
                            createCred.getCredTypeId(),
                            createCred.getCredUid()
                    ).isPresent()
            ) throw new RuntimeException("Credential type and value repeated");
        }

        Person p = personRepository.findById(createCred.getPersonId()).orElseThrow(() -> new RuntimeException("Person does not exist"));

        Credential credential = createCred.toCredential();
        credential.setPerson(p);
        credential.setCredType(credType);

        return credentialRepository.save(credential).toDto();
    }

    public Boolean uidInUse(String uid, Long credId) {
        return credentialRepository.findByDeletedFalseAndCredUidAndCredType_CredTypeIdNotAndCredIdNot(uid, 4L, credId)
                .isPresent();
    }

    public List<CredentialDto> findByPersonId(Long personId) {
        return credentialRepository
                .findAllByPersonPersonIdAndDeletedFalse(personId)
                .stream()
                .map(Credential::toDto)
                .collect(Collectors.toList());
    }

    public CredentialDto enableCredentialWithId(Long credentialId) throws Exception{
        Credential cred = credentialRepository.findById(credentialId).orElseThrow(() -> new RuntimeException("Credential does not exist"));
        cred.setIsValid(true);
        return credentialRepository.save(cred).toDto();
    }

    public CredentialDto disableCredentialWithId(Long credentialId) throws Exception{
        Credential cred = credentialRepository.findById(credentialId).orElseThrow(() -> new RuntimeException("Credential does not exist"));
        cred.setIsValid(false);
        return credentialRepository.save(cred).toDto();
    }

    public CredentialDto editCredential(EditCredentialDto editCred) throws Exception {
        credentialRepository.findByCredIdAndDeletedFalse(editCred.getCredId())
                .orElseThrow(() -> new RuntimeException("Credential does not exist"));

        CredentialType credType = credTypeRepository
                .findById(editCred.getCredTypeId())
                .orElseThrow(() -> new RuntimeException("Credential type does not exist"));

        Person person = personRepository
                .findById(editCred.getPersonId())
                .orElseThrow(() -> new RuntimeException("Person does not exist"));

        Optional<Credential> credentialOptional = credentialRepository
                .findByCredTypeCredTypeIdAndCredUidAndDeletedFalse(credType.getCredTypeId(), editCred.getCredUid());
        Optional<Credential> credentialExactCopyFromSamePerson = credentialRepository
                .findByDeletedFalseAndCredType_CredTypeIdAndCredUidAndPerson_PersonId(credType.getCredTypeId(), editCred.getCredUid(), person.getPersonId());

        // Check for duplicate exact pairing cred type and cred value iff the cred type is not pin
        if (credentialOptional.get().getCredType().getCredTypeId() != 4) {
            if (credentialOptional.isPresent() && !credentialOptional.get().getCredId().equals(editCred.getCredId())) {
                throw new RuntimeException("Credential type and value repeated");
            }
        }

        if (credentialExactCopyFromSamePerson.isPresent() && !credentialExactCopyFromSamePerson.get().getCredId().equals(editCred.getCredId())) {
            throw new RuntimeException("Credential type and value repeated from the same person");
        }

        Credential toSave = editCred.toCredential();
        toSave.setCredType(credType);
        toSave.setPerson(person);

        return credentialRepository.save(toSave).toDto();
    }

    public void deleteCredentialWithId(Long credentialId) throws Exception {
        Credential toDeleted = credentialRepository.findByCredIdAndDeletedFalse(credentialId)
                .orElseThrow(() -> new RuntimeException("Credential does not exist"));
        toDeleted.setDeleted(true);
        credentialRepository.save(toDeleted);
    }
}
