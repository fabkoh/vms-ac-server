package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Boolean existsByPersonId(Long personId);

    Optional<Person> findByPersonUidAndDeleted(String personUid,
                                               Boolean deleted);

    List<Person> findByDeleted(Boolean deleted);

    Optional<Person> findByPersonIdAndDeleted(Long personId, Boolean deleted);


    List<Person> findByAccessGroupAccessGroupIdAndDeleted(Long accessGroupId,Boolean deleted);

    Optional<Person> findByPersonMobileNumber(String personMobileNumber);

    List<Person> findByPersonEmail(String personEmail);

    Optional<Person> findByPersonMobileNumberAndDeleted(String personMobileNumber, Boolean deleted);

    Optional<Person> findByPersonEmailAndDeleted(String personEmail, Boolean deleted);


    List<Person> findByPersonIdInAndDeleted(Iterable<Long> personId, Boolean deleted);

    List<Person> findAllByAccessGroupAccessGroupIdAndDeletedFalse(Long accessGroupId);

    @Query(value = "select * from persons p where upper(:queryString) like upper(concat(personfirstname, '%')) " +
            "or upper(:queryString) like upper(concat('%', personlastname)) and deleted = false", nativeQuery = true)
    List<Person> findAllByQueryString(String queryString);
}
