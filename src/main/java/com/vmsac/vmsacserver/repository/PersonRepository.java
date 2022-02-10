package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByPersonUidAndDeleted(String personUid,
                                               Boolean deleted);

    List<Person> findByDeleted(Boolean deleted);

    Optional<Person> findByPersonIdAndDeleted(Long personId, Boolean deleted);

    List<Person> findByPersonMobileNumber(String personMobileNumber);

    List<Person> findByPersonEmail(String personEmail);

    Optional<Person> findByPersonMobileNumberAndDeleted(String personMobileNumber, Boolean deleted);

    Optional<Person> findByPersonEmailAndDeleted(String personEmail, Boolean deleted);
}
