package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByDeletedIsFalseOrderByEventTimeDesc();

    boolean existsByEventTimeEqualsAndController_ControllerSerialNoEqualsAndEventActionType_EventActionTypeId(String eventTime, String controllerSerialNo, Long eventActionTypeId);


}
