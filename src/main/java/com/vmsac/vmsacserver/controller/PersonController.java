package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.PersonDto;
import com.vmsac.vmsacserver.service.PersonService;
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
import java.util.Objects;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class PersonController {

    @Autowired
    PersonService personService;

    @GetMapping("/persons")
    public List<PersonDto> getPersons() {
        return personService.findAll();
    }

    // checks if uid is in use
    @PostMapping(path = "/person")
    public ResponseEntity<?> createPerson(
            @Valid @RequestBody PersonDto newPerson) {
        if (personService.uidInUse(newPerson.getPersonUid())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("personUid", "Person UID in use");
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(personService.save(newPerson, false),
                HttpStatus.CREATED);
    }

    // checks if person exists by id, id in path and body is the same and
    // if uid is used by another person
    @PutMapping(path = "/person/{personId}")
    public ResponseEntity<?> updatePerson(@PathVariable("personId")
                                                      Long personId,
                                          @Valid @RequestBody
                                                  PersonDto newPerson) {
        boolean invalid = false;
        Map<String, String> errors = new HashMap<>();

        if(!personService.exists(personId, false)) {
            invalid = true;
            errors.put("personId", "Person with ID " + personId + " does not exist");
        }

        if(!Objects.equals(personId, newPerson.getPersonId())) {
            invalid = true;
            errors.put("personId", "PersonId cannot be changed");
        }

        if(personService.uidInUse(newPerson.getPersonUid(), personId)) {
            invalid = true;
            errors.put("personUid", "UID " + newPerson.getPersonUid() + " is already in use");
        }

        if(invalid) {
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(personService.save(newPerson, false));
    }

    @DeleteMapping(path = "/person/{personId}")
    public ResponseEntity<?> deletePerson(@PathVariable("personId") Long personId) {
        if(!personService.exists(personId, false)) {
            Map<String, String> errors = new HashMap<>();
            errors.put("personId", "Person with ID " + personId + " does not exist");
            return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
        }
        personService.delete(personId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
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
