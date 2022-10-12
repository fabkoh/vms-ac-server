package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.notification.NotificationLogs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationLogsRepository extends JpaRepository<NotificationLogs,Long> {
    List<NotificationLogs> findByNotificationLogsIdNotNull();

}
