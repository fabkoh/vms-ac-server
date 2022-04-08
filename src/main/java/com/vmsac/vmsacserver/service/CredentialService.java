package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.credential.CreateCredentialDto;
import com.vmsac.vmsacserver.model.credential.Credential;
import com.vmsac.vmsacserver.model.credential.CredentialDto;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import com.vmsac.vmsacserver.repository.CredTypeRepository;
import com.vmsac.vmsacserver.repository.CredentialRepository;
import com.vmsac.vmsacserver.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CredentialService {

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CredTypeRepository credTypeRepository;

    public List<Credential> findAll() {
        return credentialRepository.findAll();
    }

    public CredentialDto createCredential(CreateCredentialDto createCred) throws Exception {
        Long credTypeId = createCred.getCredTypeId();

        if (credTypeId == null) throw new RuntimeException("Cred type missing");

        CredentialType credType = credTypeRepository.findById(credTypeId)
                .orElseThrow(() -> new RuntimeException("Cred type does not exist"));

        if (credentialRepository
                .findByCredTypeCredTypeIdAndCredUidAndDeletedFalse(
                        createCred.getCredTypeId(),
                        createCred.getCredUid()
                ).isPresent()
            ) throw new RuntimeException("Credential type and value repeated");

        Person p = personRepository.findById(createCred.getPersonId()).orElseThrow(() -> new RuntimeException("Person does not exist"));

        Credential credential = createCred.toCredential();
        credential.setPerson(p);
        credential.setCredType(credType);

        return credentialRepository.save(credential).toDto();
    }
}
