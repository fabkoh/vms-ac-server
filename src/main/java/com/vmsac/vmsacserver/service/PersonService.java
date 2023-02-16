package com.vmsac.vmsacserver.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmsac.vmsacserver.model.CreatePersonDto;
import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.PersonOnlyDto;
import com.vmsac.vmsacserver.model.PersonDto;
import com.vmsac.vmsacserver.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    // NOTE: ...NotDeleted and ...InUse methods implies deleted == false

    // read methods
    public List<PersonDto> findAllNotDeleted() {
        return personRepository.findByDeleted(false).stream()
                .filter(p -> !p.getDeleted())
                .map(Person::toDto)
                .collect(Collectors.toList());
    }

    public Optional<Person> findByIdInUse(Long personId) {
        return personRepository.findByPersonIdAndDeleted(personId, false);
    }

    public List<Person> findByAccGrpId(Long id, Boolean deleted){
        return personRepository.findByAccessGroupAccessGroupIdAndDeleted(id,deleted);
    }

    // checkers
    public Boolean uidInUse(String uid) {
        return personRepository.findByPersonUidAndDeleted(uid, false)
                .isPresent();
    }

    //edit: check if personid is the same as personname
    public Boolean uidInUse(String uid, Long personId) {
        Optional<Person> personOptional = personRepository
                .findByPersonUidAndDeleted(uid, false);
        return personOptional.isPresent() &&
                !Objects.equals(personOptional.get().getPersonId(), personId);
    }

    public Boolean mobileNumberInUse(String personMobileNumber) {
        return personRepository.findByPersonMobileNumberAndDeleted(personMobileNumber, false)
                .isPresent();
    }

    public Boolean emailInUse(String email) {
        return personRepository.findByPersonEmail(email).size() > 0;
    }

    public Boolean idInUse(Long id) {
        return personRepository.findByPersonIdAndDeleted(id, false).isPresent();
    }


    // create / update methods
    public PersonDto createNotDeleted(CreatePersonDto personDto) {
        return personRepository.save(personDto.toPerson(false)).toDto();
    }


    //for deletion
    public PersonDto save(PersonDto personDto, Boolean deleted) {
        return personRepository.save(personDto.toPerson(deleted)).toDto();
    }

    //normal save
    public Person save(Person person) {
        return personRepository.save(person);
    }


    // helpers (if other controllers need can move to util)
    public String generateUid() {
        String randomString;

        do {
            randomString = generateRandomString(8);
        } while (uidInUse(randomString));
        return randomString;
    }

    private String generateRandomString(int length) {
        int leftLimit = 48; // "0"
        int rightLimit = 122; // "z"
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> i <= 57 || (i >= 65 && i <= 90) || i >= 97)
                // "9" and below OR "A" to "Z" OR "a" and above
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

    }

    //converts list of persononlydto to list of person
    public List<Person> personsList(List<PersonOnlyDto> stagedPersons){
        return  personRepository.findByPersonIdInAndDeleted(stagedPersons.stream().map(PersonOnlyDto::getPersonId).collect(Collectors.toList()),false);
    }

}
