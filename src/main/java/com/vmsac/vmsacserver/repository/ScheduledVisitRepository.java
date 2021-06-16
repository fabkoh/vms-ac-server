package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledVisitRepository extends JpaRepository<ScheduledVisit, Long> {
}
