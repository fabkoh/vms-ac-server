package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.CreatePersonDto;

import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.PersonDto;
import com.vmsac.vmsacserver.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

        return new ResponseEntity<>(personService.createNotDeleted(newPersonDto),
                HttpStatus.CREATED);
    }

    // checks if uid is blank, id in database and uid used by another person
    // TODO: checking of uid and is in database calls database search twice,
    //  implement both checks at the same time
    @PutMapping(path = "/person")
    public ResponseEntity<?> updatePerson(
            @Valid @RequestBody PersonDto updatePersonDto) {

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

        return ResponseEntity.ok(personService.save(updatePersonDto, false));
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
        deletePerson.setDeleted(true);
        personService.save(deletePerson);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
