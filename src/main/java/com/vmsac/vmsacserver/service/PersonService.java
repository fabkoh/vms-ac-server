package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.PersonDto;
import com.vmsac.vmsacserver.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    public Boolean uidInUse(String uid) {
        return personRepository.findByPersonUidAndDeleted(uid, false)
                .isPresent();
    }

    // returns if the uid is in use by another person
    public Boolean uidInUse(String uid, Long personId) {
        Optional<Person> personOptional = personRepository.findByPersonUidAndDeleted(uid, false);
        return personOptional.isPresent() && !Objects.equals(personOptional.get().getPersonId(), personId);
    }

    public PersonDto save(PersonDto personDto, Boolean deleted) {
        return personRepository.save(personDto.toPerson(deleted)).toDto();
    }

    public List<PersonDto> findAll() {
        return personRepository.findByDeleted(false).stream()
                .filter(p -> !p.getDeleted())
                .map(Person::toDto)
                .collect(Collectors.toList());
    }

    public Boolean exists(Long personId, Boolean deleted) {
        return personRepository.findByPersonIdAndDeleted(personId, deleted).isPresent();
    }

    public Optional<Person> findByIdAndDeleted(Long personId, Boolean deleted) {
        return personRepository.findByPersonIdAndDeleted(personId, deleted);
    }

    public void delete(Person person) {
        person.setDeleted(true);
        personRepository.save(person);
    }
}
