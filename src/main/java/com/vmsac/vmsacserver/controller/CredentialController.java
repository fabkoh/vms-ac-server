package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.credential.CreateCredentialDto;
import com.vmsac.vmsacserver.model.credential.Credential;
import com.vmsac.vmsacserver.model.credential.CredentialDto;
import com.vmsac.vmsacserver.model.credential.EditCredentialDto;
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
    public List<CredentialDto> findAll(@RequestParam(name="personid", required = false) Long personId) {
        if (personId == null) return credentialService.findAll();

        return credentialService.findByPersonId(personId);
    }

    @PostMapping("/credential")
    public ResponseEntity<?> createCredential(@RequestBody CreateCredentialDto createCred) {
        try {
            return new ResponseEntity(credentialService.createCredential(createCred), HttpStatus.CREATED);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/credential/{credentialId}/enable")
    public ResponseEntity<?> enableCredentialWithId(@PathVariable Long credentialId) {
        try {
            return ResponseEntity.ok(credentialService.enableCredentialWithId(credentialId));
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/credential/{credentialId}/disable")
    public ResponseEntity<?> disableCredentialWithId(@PathVariable Long credentialId) {
        try {
            return ResponseEntity.ok(credentialService.disableCredentialWithId(credentialId));
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/credential")
    public ResponseEntity<?> editCredential(@RequestBody EditCredentialDto credential) {
        try {
            return ResponseEntity.ok(credentialService.editCredential(credential));
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/credential/{credentialId}")
    public ResponseEntity<?> deleteCredentialWithId(@PathVariable Long credentialId) {
        try {
            credentialService.deleteCredentialWithId(credentialId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
