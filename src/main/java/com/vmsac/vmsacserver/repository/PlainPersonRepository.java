package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.PlainPerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlainPersonRepository extends JpaRepository<PlainPerson, Long> {

}
