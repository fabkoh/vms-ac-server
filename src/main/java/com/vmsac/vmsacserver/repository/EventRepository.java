package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByDeletedIsFalseOrderByEventTimeDesc(Pageable pageable);

    @Query(value = "SELECT * FROM events e WHERE " +
            "eventactiontypeid IN (:eventTypeIds) " +
            "AND entranceid IN :entranceIds AND controllerid IN :controllerIds AND personid IN :personIds " +
            "AND accessgroupid IN :accessGroupIds AND deleted = false " +
            "AND to_timestamp(e.eventTime, 'MM-DD-YYYY HH24:MI:SS') BETWEEN :start AND :end " +
            "ORDER BY e.eventTime DESC",
            nativeQuery = true)
    List<Event> findByQueryString2(
            @Param("eventTypeIds") List<Long> eventTypeIds,
            @Param("entranceIds") List<Long> entranceIds,
            @Param("controllerIds") List<Long> controllerIds,
            @Param("personIds") List<Long> personIds,
            @Param("accessGroupIds") List<Long> accessGroupIds,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end,
            Pageable pageable);


    boolean existsByEventTimeEqualsAndController_ControllerSerialNoEqualsAndEventActionType_EventActionTypeId(String eventTime, String controllerSerialNo, Long eventActionTypeId);

    // eventActionInputType = 6 for fire

    List<Event> findByEventActionType_EventActionTypeIdEquals(@Nullable Long eventActionTypeId);
    @Query(value = "SELECT * FROM events e WHERE " +
            "eventactiontypeid = (:eventTypeId) " +
            "AND to_timestamp(e.eventTime, 'MM-DD-YYYY HH24:MI:SS') BETWEEN :start AND :end " +
            "ORDER BY e.eventTime DESC",
            nativeQuery = true)
    List<Event> findEventIn24hrs(
            @Param("start") Timestamp start,
            @Param("end") Timestamp end,
            @Param("eventTypeId") Long eventTypeId);
}