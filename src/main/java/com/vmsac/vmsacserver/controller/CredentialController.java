package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.credential.CreateCredentialDto;
import com.vmsac.vmsacserver.model.credential.Credential;
import com.vmsac.vmsacserver.model.credential.CredentialDto;
import com.vmsac.vmsacserver.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class CredentialController {

    @Autowired
    private CredentialService credentialService;

    @GetMapping("/credentials")
    public List<Credential> findAll() {
        return credentialService.findAll();
    }

    @PostMapping("/credential")
    public ResponseEntity<?> create(@RequestBody CreateCredentialDto createCred) {
        try {
            return new ResponseEntity(credentialService.createCredential(createCred), HttpStatus.CREATED);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
