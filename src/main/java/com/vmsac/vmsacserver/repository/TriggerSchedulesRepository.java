package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.TriggerSchedules;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TriggerSchedulesRepository extends JpaRepository<TriggerSchedules, Long> {

    Optional<TriggerSchedules> findByDeletedFalseAndAndTriggerScheduleId(Long triggerSchedulesId);
}
