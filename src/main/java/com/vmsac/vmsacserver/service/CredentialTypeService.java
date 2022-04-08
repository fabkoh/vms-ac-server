package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import com.vmsac.vmsacserver.repository.CredTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CredentialTypeService {

    @Autowired
    private CredTypeRepository credTypeRepository;

    public List<CredentialType> findAll() {
        return credTypeRepository.findAll();
    }
}
