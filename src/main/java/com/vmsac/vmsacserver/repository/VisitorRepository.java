package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Optional<Visitor> findByVisitorUid(String visitorUid);

    boolean existsByVisitorUid(String visitorUid);

    @Query("select distinct v from Visitor v left join fetch v.visitorScheduledVisits where v.visitorUid = :uid")
    Optional<Visitor> findByVisitorUidWithScheduledVisits(@Param("uid") String uid);
}
