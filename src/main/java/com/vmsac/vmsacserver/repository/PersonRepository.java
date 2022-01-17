package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByPersonUidAndDeleted(String personUid,
                                               Boolean deleted);

}
