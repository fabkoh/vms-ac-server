package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PersonRepository extends JpaRepository<Person, Long>{
}
