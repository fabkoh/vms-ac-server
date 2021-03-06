package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScheduledVisitRepository extends JpaRepository<ScheduledVisit, Long> {

    List<ScheduledVisit> findByScheduledVisitId(Long scheduledVisitId);


    List<ScheduledVisit> findByQrCodeId(String qrCodeId);

}
