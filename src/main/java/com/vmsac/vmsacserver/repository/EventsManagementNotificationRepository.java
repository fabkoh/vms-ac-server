package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventsManagementNotificationRepository extends JpaRepository<EventsManagementNotification,Long> {
}
