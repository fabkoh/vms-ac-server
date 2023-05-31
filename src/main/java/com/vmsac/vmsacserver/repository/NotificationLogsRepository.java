package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.notification.NotificationLogs;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;

import java.util.List;

public interface NotificationLogsRepository extends JpaRepository<NotificationLogs,Long> {
    List<NotificationLogs> findByNotificationLogsIdNotNull();

    @Query(value = "select * from notificationlogs n where " +
            "eventsmanagementnotificationid IN :notificationIds " +
            "n WHERE TO_TIMESTAMP(n.timesent, 'MM-DD-YYYY HH24:MI:SS') BETWEEN :start AND :end" +
            "order by n.timesent desc", nativeQuery = true)
    List<NotificationLogs> findByQueryString(List<Long> notificationIds, Timestamp start, Timestamp end, Pageable pageable);

    @Query(value = "select * from notificationlogs n where " +
            "to_timestamp(timesent, 'mm-dd-yyyy HH24:MI:SS') >= :start " +
            "order by timesent desc", nativeQuery = true)
    List<NotificationLogs> findByTimeStart(Timestamp start, Pageable pageable);

    @Query(value = "select * from notificationlogs n where " +
            "to_timestamp(timesent, 'mm-dd-yyyy HH24:MI:SS') <= :end " +
            "order by timesent desc", nativeQuery = true)
    List<NotificationLogs> findByTimeEnd(Timestamp end, Pageable pageable);

//    @Query(value = "select * from notificationlogs n where " +
//            "AND to_timestamp(n.timesent, 'mm-dd-yyyy HH24:MI:SS') between :start and :end " +
//            "order by timesent desc", nativeQuery = true)
//    List<NotificationLogs> findByTime(Timestamp start, Timestamp end, Pageable pageable);

    @Query(value = "SELECT * FROM notificationlogs n WHERE TO_TIMESTAMP(n.timesent, 'MM-DD-YYYY HH24:MI:SS') BETWEEN :start AND :end ORDER BY n.timesent DESC", nativeQuery = true)
    List<NotificationLogs> findByTime(@Param("start") Timestamp start, @Param("end") Timestamp end, Pageable pageable);

    List<NotificationLogs> findByOrderByTimeSentDesc(Pageable pageable);

}
