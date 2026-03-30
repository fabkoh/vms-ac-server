package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Optional<Visitor> findByVisitorUid(String visitorUid);

    boolean existsByVisitorUid(String visitorUid);
}
