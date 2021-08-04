package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.ActualVisitLogs;
import com.vmsac.vmsacserver.model.ScheduledVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActualVisitLogRepository extends JpaRepository<ActualVisitLogs, Long> {

    List<ActualVisitLogs> findBylogId(Long logId);

}
