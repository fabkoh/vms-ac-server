package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import com.vmsac.vmsacserver.security.service.CredentialTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/credential-types")
public class CredentialTypeController {

    @Autowired
    private CredentialTypeService credentialTypeService;

    @GetMapping
    public List<CredentialType> findAll() {
        return credentialTypeService.findAll();
    }
}
