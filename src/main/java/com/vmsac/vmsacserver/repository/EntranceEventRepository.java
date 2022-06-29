package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.EntranceEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EntranceEventRepository extends JpaRepository<EntranceEvent,Long> {
    List<EntranceEvent> findByDeletedIsFalseOrderByEventTimeDesc();

    boolean existsByEventTimeEqualsAndEntrance_EntranceIdEqualsAndEntranceEventType_ActionTypeId
            (LocalDateTime eventTime, Long entraceId, Long actionTypeId);
}
