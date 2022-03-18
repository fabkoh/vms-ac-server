package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Access;
import java.util.List;
import java.util.Optional;

public interface AccessGroupScheduleRepository extends JpaRepository<AccessGroupSchedule,Long> {

    Optional<AccessGroupSchedule> findByAccessGroupScheduleIdAndDeleted(Long accessGroupScheduleId, Boolean deleted);

    List<AccessGroupSchedule> findAllByGroupToEntranceIdInAndDeletedFalse(List<Long> groupToEntranceIds);

    Optional<AccessGroupSchedule> findByAccessGroupScheduleIdAndDeletedFalse(Long accessGroupScheduleId);

    List<AccessGroupSchedule> findAllByGroupToEntranceIdAndDeletedFalse(Long groupToEntranceIds);

    List<AccessGroupSchedule> findAllByDeletedFalse();

}
