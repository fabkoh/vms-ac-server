package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByDeletedIsFalseOrderByEventTimeDesc(Pageable pageable);

    //    @Query(value = "select * from events where to_timestamp(eventtime, 'mm-dd-yyyy HH24:MI:SS') " +
//            ">= start", nativeQuery = true)
//    List<Event> refreshEvents(LocalDateTime start)
//    @Query(value = "select * from events e where eventactiontypeid IN :eventTypeIds " +
//            "or entranceid IN :entranceIds or controllerid IN :controllerIds or personid IN :personIds " +
//            "or accessgroupid IN :accessGroupIds and deleted = false " +
//            "and to_timestamp(e.eventTime, 'MM-DD-YYYY HH24:MI:SS') between :start and :end " +
//            "order by e.eventTime desc",
//            nativeQuery = true)
//    List<Event> findByQueryString(List<Long> eventTypeIds, List<Long> entranceIds, List<Long> controllerIds,
//                                  List<Long> personIds, List<Long> accessGroupIds, Timestamp start, Timestamp end,
//                                  Pageable pageable);

//    @Query(value = "SELECT * FROM events e WHERE " +
//            "eventactiontypeid IN :eventTypeIds " +
//            "OR entranceid IN :entranceIds OR controllerid IN :controllerIds OR personid IN :personIds " +
//            "OR accessgroupid IN :accessGroupIds AND deleted = false " +
//            "AND to_timestamp(e.eventTime, 'YYYY-MM-DD HH:MI:SS.S') BETWEEN :start AND :end " +
//            "ORDER BY e.eventTime DESC",
//            nativeQuery = true)
//    List<Event> findByQueryString(
//            @Param("eventTypeIds") List<Long> eventTypeIds,
//            @Param("entranceIds") List<Long> entranceIds,
//            @Param("controllerIds") List<Long> controllerIds,
//            @Param("personIds") List<Long> personIds,
//            @Param("accessGroupIds") List<Long> accessGroupIds,
//            @Param("start") Timestamp start,
//            @Param("end") Timestamp end,
//            Pageable pageable);

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


}