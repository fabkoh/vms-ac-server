package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByDeletedIsFalseOrderByEventTimeDesc(Pageable pageable);

    @Query(value = "select * from events e where eventactiontypeid IN :eventTypeIds " +
            "or entranceid IN :entranceIds or controllerid IN :controllerIds or personid IN :personIds " +
            "or accessgroupid IN :accessGroupIds and deleted = false " +
            "order by eventtime", nativeQuery = true)
    List<Event> findByQueryString(List<Long> eventTypeIds, List<Long> entranceIds, List<Long> controllerIds,
                                  List<Long> personIds, List<Long> accessGroupIds);

    boolean existsByEventTimeEqualsAndController_ControllerSerialNoEqualsAndEventActionType_EventActionTypeId(String eventTime, String controllerSerialNo, Long eventActionTypeId);


}