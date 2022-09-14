package com.vmsac.vmsacserver.controller;


import com.vmsac.vmsacserver.model.*;

import com.vmsac.vmsacserver.model.credential.CredentialDto;
import com.vmsac.vmsacserver.service.AccessGroupService;
import com.vmsac.vmsacserver.service.CredentialService;
import com.vmsac.vmsacserver.service.PersonService;
import com.vmsac.vmsacserver.util.UniconUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class PersonController {

    @Autowired
    PersonService personService;
    @Autowired
    AccessGroupService AccessGroupService;
    @Autowired
    CredentialService credentialService;

    @GetMapping("/persons")
    public List<PersonDto> getPersons() {
        return personService.findAllNotDeleted();
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<?> getPerson(@PathVariable Long personId) {
        Optional<Person> optionalPerson = personService.findByIdInUse(personId);

        if(optionalPerson.isPresent()) {
            return ResponseEntity.ok(optionalPerson.get().toDto());
        }

        Map<String, String> errors = new HashMap<>();
        errors.put("personId", "Person with Id " +
                personId + " does not exist");

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/person/uid/{uid}")
    public Boolean uidExists(@PathVariable("uid") String uid) {
        return personService.uidInUse(uid);
    }

    @GetMapping("/person/uid/{id}/{uid}")
    public Boolean uidInUse(@PathVariable("uid") String uid,
    @PathVariable("id") Long id) {
        return personService.uidInUse(uid, id);
    }
    // checks if uid is in use
    @PostMapping(path = "/person")
    public ResponseEntity<?> createPerson(
            @Valid @RequestBody CreatePersonDto newPersonDto) {

        if(newPersonDto.getPersonUid() == null || newPersonDto.getPersonUid().isBlank()) {
            newPersonDto.setPersonUid(personService.generateUid());
        }else if (personService.uidInUse(newPersonDto.getPersonUid())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("personUid", "Person UID " +
                    newPersonDto.getPersonUid() + " in use");
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        }
        if (newPersonDto.getAccessGroup() != null){
            Long accessGroupId = newPersonDto.getAccessGroup().getAccessGroupId();
            if(AccessGroupService.findById(accessGroupId).isEmpty()){
                Map<Long, String> errors = new HashMap<>();
                errors.put(accessGroupId, "accessGroupId " +
                        accessGroupId + " does not exist");
                return new ResponseEntity<>(errors,HttpStatus.NOT_FOUND);
            }
            AccessGroup accessGroup = AccessGroupService.findById(accessGroupId).get();
            newPersonDto.setAccessGroup(accessGroup);

            PersonDto personDto = personService.createNotDeleted(newPersonDto);

            return new ResponseEntity<>(personDto,
                    HttpStatus.CREATED);
        }

        // check if phone number is empty
        if (newPersonDto.getPersonMobileNumber().equals("+") || newPersonDto.getPersonMobileNumber().equals("+65")) {
            newPersonDto.setPersonMobileNumber("");
        }

        PersonDto personDto = personService.createNotDeleted(newPersonDto);

        return new ResponseEntity<>(personDto,
                HttpStatus.CREATED);
    }

    // checks if uid is blank, id in database and uid used by another person
    // TODO: checking of uid and is in database calls database search twice,
    //  implement both checks at the same time
    @PutMapping(path = "/person")
    public ResponseEntity<?> updatePerson(
            @Valid @RequestBody PersonDto updatePersonDto) {

        // check if phone number is empty
        if (updatePersonDto.getPersonMobileNumber().equals("+") || updatePersonDto.getPersonMobileNumber().equals("+65")) {
            updatePersonDto.setPersonMobileNumber("");
        }

        if(!personService.idInUse(updatePersonDto.getPersonId())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("personId", "Person with Id " +
                    updatePersonDto.getPersonId() + " does not exist");
            return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
        }

        if(personService.uidInUse(
                updatePersonDto.getPersonUid(), updatePersonDto.getPersonId())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("personUid", "Person UID " +
                    updatePersonDto.getPersonUid() + " is already in use");
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        }
        else if (updatePersonDto.getAccessGroup() != null){
            Long accessGroupId = updatePersonDto.getAccessGroup().getAccessGroupId();
            if(!AccessGroupService.findById(accessGroupId).isPresent()){
                Map<Long, String> errors = new HashMap<>();
                errors.put(accessGroupId, "accessGroupId " +
                        accessGroupId + " does not exist");
                return new ResponseEntity<>(errors,HttpStatus.NOT_FOUND);
            }
            AccessGroup accessGroup = AccessGroupService.findById(accessGroupId).get();
            updatePersonDto.setAccessGroup(accessGroup.toAccessGroupOnlyDto());
            PersonDto personDto = personService.save(updatePersonDto, false);

            return new ResponseEntity<>(personDto,
                    HttpStatus.OK);
        }
        PersonDto personDto = personService.save(updatePersonDto, false);

        return ResponseEntity.ok(personDto);
    }

    @DeleteMapping(path = "/person/{personId}")
    public ResponseEntity<?> deletePerson(
            @PathVariable("personId") Long personId) {

        Optional<Person> optionalPerson =
                personService.findByIdInUse(personId);

        if(optionalPerson.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("personId",
                    "Person with ID " + personId + " does not exist");
            return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
        }

        Person deletePerson = optionalPerson.get();
        List<CredentialDto> credentials = credentialService.findByPersonId(personId);
        credentials.forEach(credential -> {
            try {
                credentialService.deleteCredentialWithId(credential.getCredId());
            } catch (Exception e) {
                // ignore exception as cred id should always be valid
            }
        });
        deletePerson.setDeleted(true);
        deletePerson.setAccessGroup(null);
        personService.save(deletePerson);


        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/person/mobileNumber/{mobileNumber}")
    public Boolean mobileNumberExists(@PathVariable("mobileNumber") String mobileNumber) {
        return personService.mobileNumberInUse(mobileNumber);
    }

    @GetMapping("/person/email/{email}")
    public Boolean emailExists(@PathVariable("email") String email) {
        return personService.emailInUse(email);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
