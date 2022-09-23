package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.notification.NotificationLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogsRepository extends JpaRepository<NotificationLogs,Long> {
}
