package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledVisitRepository extends JpaRepository<ScheduledVisit, Long> {

    List<ScheduledVisit> findByScheduledVisitId(String scheduledVisitId);
}
