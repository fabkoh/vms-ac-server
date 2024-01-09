package com.vmsac.vmsacserver.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.CreatePersonDto;
import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.PersonDto;
import com.vmsac.vmsacserver.model.credential.CreateCredentialDto;
import com.vmsac.vmsacserver.model.credential.CredentialDto;
import com.vmsac.vmsacserver.service.AccessGroupService;
import com.vmsac.vmsacserver.service.CredentialService;
import com.vmsac.vmsacserver.service.PersonService;
import com.vmsac.vmsacserver.util.CsvToJson;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
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

        if (optionalPerson.isPresent()) {
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

        if (newPersonDto.getPersonUid() == null || newPersonDto.getPersonUid().isBlank()) {
            newPersonDto.setPersonUid(personService.generateUid());
        } else if (personService.uidInUse(newPersonDto.getPersonUid())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("personUid", "Person UID " +
                    newPersonDto.getPersonUid() + " in use");
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        }
        if (newPersonDto.getAccessGroup() != null) {
            Long accessGroupId = newPersonDto.getAccessGroup().getAccessGroupId();
            if (AccessGroupService.findById(accessGroupId).isEmpty()) {
                Map<Long, String> errors = new HashMap<>();
                errors.put(accessGroupId, "accessGroupId " +
                        accessGroupId + " does not exist");
                return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
            }
            AccessGroup accessGroup = AccessGroupService.findById(accessGroupId).get();
            newPersonDto.setAccessGroup(accessGroup);

            PersonDto personDto = personService.createNotDeleted(newPersonDto);

            return new ResponseEntity<>(personDto,
                    HttpStatus.CREATED);
        }


        // check if phone number is empty
        String mobileNumber = newPersonDto.getPersonMobileNumber();
        if (mobileNumber == null || mobileNumber.equals("+") || mobileNumber.equals("+65")) {
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
        if (updatePersonDto.getPersonMobileNumber() == null ||
                updatePersonDto.getPersonMobileNumber().equals("+") ||
                updatePersonDto.getPersonMobileNumber().equals("+65")) {
            updatePersonDto.setPersonMobileNumber("");
        }

        if (!personService.idInUse(updatePersonDto.getPersonId())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("personId", "Person with Id " +
                    updatePersonDto.getPersonId() + " does not exist");
            return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
        }

        if (personService.uidInUse(
                updatePersonDto.getPersonUid(), updatePersonDto.getPersonId())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("personUid", "Person UID " +
                    updatePersonDto.getPersonUid() + " is already in use");
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        } else if (updatePersonDto.getAccessGroup() != null) {
            Long accessGroupId = updatePersonDto.getAccessGroup().getAccessGroupId();
            if (!AccessGroupService.findById(accessGroupId).isPresent()) {
                Map<Long, String> errors = new HashMap<>();
                errors.put(accessGroupId, "accessGroupId " +
                        accessGroupId + " does not exist");
                return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
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

        if (optionalPerson.isEmpty()) {
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

    //    @RestController
//    @RequestMapping("api/person/importcsv")
//    public class FileUploadController {
//
//        @PostMapping
//        public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
//            // Read the contents of the CSV file
//            List<String[]> rows = new ArrayList<>();
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    rows.add(line.split(","));
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            System.out.println(rows);
//
//            // Do something with the contents of the file (e.g. store in a database)
//
//            return ResponseEntity.ok("File uploaded successfully");
//        }
//    }
//
    private String jsonResult = null;

    @CrossOrigin
    @RestController

    public class FileUploadController {
        @PostMapping("/api/person/importcsv")
        public void handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
            // Check if the file is empty
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                CsvListReader csvReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);
                String[] header = csvReader.getHeader(true);
                // Do something with the header values

                String[] expectedHeader = {"\uFEFFFirst Name", "Last Name", "UID", "Email", "Mobile Number", "Credential type", "Credential pin", "Credential Expiry (YYYY-MM-DD HOUR-MIN-SEC)"};
                if (header.length != expectedHeader.length) {
                    throw new IllegalArgumentException("Header length does not match the expected header length");
                }

                for (int i = 0; i < header.length; i++) {
                    if (!header[i].trim().equalsIgnoreCase(expectedHeader[i].trim())) {
                        throw new IllegalArgumentException(header[i] + " Header value does not match the expected header value " + expectedHeader[i]);
                    }
                }

                System.out.println("NO ERRORS!!!");

//                Convert csv file to json with empty strings as null

                try {
                    CsvToJson csvToJson = new CsvToJson(credentialService, personService);
                    jsonResult = csvToJson.convert(file);
                    System.out.println("Result: " + jsonResult);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CsvValidationException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @GetMapping("/person/importcsv/json")
    public String getJson() {
        if (jsonResult == null) {
            throw new RuntimeException("JSON data not available");
        }
        System.out.println("Json get activated");
        return jsonResult;
    }

    @CrossOrigin
    @PostMapping("person/importcsv/greenData")
    public void postGreenData(@RequestBody String stringData) throws Exception {
        System.out.println(stringData);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(stringData);

        for (JsonNode node : jsonNode) {
            System.out.println(node);

            String jsonStr = node.toString();
            JSONObject jsonObj = new JSONObject(jsonStr);

            CreatePersonDto newPersonDto = new CreatePersonDto();
            newPersonDto.setPersonFirstName(node.get("personFirstName").asText());
            newPersonDto.setPersonLastName(node.get("personLastName").asText());
            newPersonDto.setPersonEmail(node.get("personEmail").asText());
            newPersonDto.setPersonMobileNumber(node.get("personMobileNumber").asText());
            newPersonDto.setPersonUid(node.get("personUid").asText());

            if (newPersonDto.getPersonUid() == null || newPersonDto.getPersonUid().isBlank() || personService.uidInUse(newPersonDto.getPersonUid())) {
                newPersonDto.setPersonUid(personService.generateUid());
            }

// Call the createNotDeleted() function to create a new person

             personService.createNotDeleted(newPersonDto);


// Print the newly created person
            System.out.println(newPersonDto);

            Optional<Person> person = personService.findByUid(String.valueOf(newPersonDto.getPersonUid()));
            System.out.println(person.isEmpty());
            System.out.println(person.get());

            if (!node.get("credentialPin").asText().isEmpty()
                    && !node.get("credentialExpiry").asText().isEmpty()
                    && !node.get("credentialType").asText().isEmpty()
            ) {
                CreateCredentialDto newCredential = new CreateCredentialDto();
                newCredential.setCredUid(node.get("credentialPin").asText());
                newCredential.setCredTTL(LocalDateTime.parse(node.get("credentialExpiry").asText()));

                long credType = 1;
                if (node.get("credentialType").asText().equals("Card") ) {
                    credType = 1;
                } else if
                (node.get("credentialType").asText().equals("Face") ) {
                    credType = 2;
                } else if
                (node.get("credentialType").asText().equals("Fingerprint") ) {
                    credType = 3;
                } else if
                (node.get("credentialType").asText().equals("Pin")) {
                    credType = 4;
                }
                newCredential.setCredTypeId(credType);
                newCredential.setValid(Boolean.TRUE);
                newCredential.setPerm(Boolean.FALSE);
                newCredential.setIsValid(Boolean.TRUE);
                if (!person.isEmpty()) {
                    newCredential.setPersonId(person.get().getPersonId());
                }

                System.out.println(newCredential);

                try {
                    credentialService.createCredential(newCredential);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}