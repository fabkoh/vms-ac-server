package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.AccessGroupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessGroupScheduleRepository extends JpaRepository<AccessGroupSchedule,Long> {

    Optional<AccessGroupSchedule> findByAccessGroupScheduleIdAndDeleted(Long accessGroupScheduleId, Boolean deleted);

}
