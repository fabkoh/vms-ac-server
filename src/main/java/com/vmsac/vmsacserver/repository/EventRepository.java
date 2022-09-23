package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByDeletedIsFalseOrderByEventTimeDesc(Pageable pageable);

//    @Query(value = "select * from events where to_timestamp(eventtime, 'mm-dd-yyyy HH24:MI:SS') " +
//            ">= start", nativeQuery = true)
//    List<Event> refreshEvents(LocalDateTime start)
    @Query(value = "select * from events e where eventactiontypeid IN :eventTypeIds " +
            "or entranceid IN :entranceIds or controllerid IN :controllerIds or personid IN :personIds " +
            "or accessgroupid IN :accessGroupIds and deleted = false " +
            "and to_timestamp(eventtime, 'mm-dd-yyyy HH24:MI:SS') between :start and :end " +
            "order by eventtime desc", nativeQuery = true)
    List<Event> findByQueryString(List<Long> eventTypeIds, List<Long> entranceIds, List<Long> controllerIds,
                                  List<Long> personIds, List<Long> accessGroupIds, Timestamp start, Timestamp end,
                                  Pageable pageable);

    boolean existsByEventTimeEqualsAndController_ControllerSerialNoEqualsAndEventActionType_EventActionTypeId(String eventTime, String controllerSerialNo, Long eventActionTypeId);


}