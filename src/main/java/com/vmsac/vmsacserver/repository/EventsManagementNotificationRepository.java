package com.vmsac.vmsacserver.repository;

import java.util.List;
import java.util.Optional;

import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventsManagementNotificationRepository extends JpaRepository<EventsManagementNotification,Long> {
    List<EventsManagementNotification> findByDeletedFalse();
    List<EventsManagementNotification> findByEventsManagement_EventsManagementIdAndDeletedFalse(Long eventsManagementId);
    Optional<EventsManagementNotification> findByEventsManagementNotificationIdAndDeletedFalse(Long eventsManagementNotificationId);
    Optional<EventsManagementNotification> findByDeletedFalseAndEventsManagementNotificationTypeAndEventsManagement_EventsManagementId(String eventsManagementNotificationType, Long eventsManagementId);
}
