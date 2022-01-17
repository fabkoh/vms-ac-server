package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.PersonDto;
import com.vmsac.vmsacserver.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    public Boolean uidInUse(String UID) {
        return personRepository.findByPersonUidAndDeleted(UID, false)
                .isPresent();
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
}
