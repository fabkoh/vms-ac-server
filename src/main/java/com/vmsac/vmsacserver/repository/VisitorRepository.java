package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;


public interface VisitorRepository extends JpaRepository<Visitor, Long>{
    Visitor findByLastFourDigitsOfId(String lastFourDigitsOfId);
}
