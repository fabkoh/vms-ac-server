package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.credential.CreateCredentialDto;
import com.vmsac.vmsacserver.model.credential.CredentialDto;
import com.vmsac.vmsacserver.model.credential.EditCredentialDto;
import com.vmsac.vmsacserver.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class CredentialController {

    @Autowired
    private CredentialService credentialService;

    @GetMapping("/credentials")
    public List<CredentialDto> findAll(@RequestParam(name="personid", required = false) Long personId) {
        if (personId == null) return credentialService.findAllNotDeleted();

        return credentialService.findByPersonId(personId);
    }

    @PostMapping("/credential")
    public ResponseEntity<?> createCredential(@RequestBody CreateCredentialDto createCred) {
        CredentialDto credential;
        if (createCred.getCredTypeId() != 4) {
            if (credentialService.uidInUse(createCred.getCredUid(), createCred.getCredId())) {
                Map<Long, String> errors = new HashMap<>();
                // as currently only cred uid error is being returned like this, we can use credId as key instead
                errors.put(createCred.getCredId(), "cred value in use");
                return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
        }
        try {
            credential = credentialService.createCredential(createCred);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(credential, HttpStatus.CREATED);
    }

    @PutMapping("/credential/{credentialId}/enable")
    public ResponseEntity<?> enableCredentialWithId(@PathVariable Long credentialId) {
        CredentialDto credential;
        try {
            credential = credentialService.enableCredentialWithId(credentialId);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(credential);
    }

    @PutMapping("/credential/{credentialId}/disable")
    public ResponseEntity<?> disableCredentialWithId(@PathVariable Long credentialId) {
        CredentialDto credential;
        try {
            credential = credentialService.disableCredentialWithId(credentialId);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(credential);
    }

    @PutMapping("/credential")
    public ResponseEntity<?> editCredential(@RequestBody EditCredentialDto credential) {
        CredentialDto cred;
        // Cred type ID of pin is 4
        if (credential.getCredTypeId() != 4) {
            if (credentialService.uidInUse(credential.getCredUid(), credential.getCredId())) {
                Map<Long, String> errors = new HashMap<>();
                // as currently only cred uid error is being returned like this, we can use credId as key instead
                errors.put(credential.getCredId(), "cred value in use");
                return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
        }
        try {
            cred = credentialService.editCredential(credential);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(cred);
    }

    @DeleteMapping("/credential/{credentialId}")
    public ResponseEntity<?> deleteCredentialWithId(@PathVariable Long credentialId) {
        try {
            credentialService.deleteCredentialWithId(credentialId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
