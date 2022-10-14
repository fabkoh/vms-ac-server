package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntranceScheduleRepository extends JpaRepository<EntranceSchedule,Long> {

    Optional<EntranceSchedule> findByEntranceScheduleIdAndDeleted(Long entranceScheduleId, Boolean deleted);

    List<EntranceSchedule> findAllByEntranceIdInAndDeletedFalse(List<Long> entranceIds);

    Optional<EntranceSchedule> findByEntranceScheduleIdAndDeletedFalse(Long entranceScheduleId);

    List<EntranceSchedule> findAllByEntranceIdAndDeletedFalse(Long entranceIds);

    List<EntranceSchedule> findByEntranceIdAndDeletedFalseAndIsActiveTrue(Long entranceId);

    List<EntranceSchedule> findAllByDeletedFalse();


}
