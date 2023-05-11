package com.vmsac.vmsacserver.repository;

import java.util.List;
import java.util.Optional;

import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventsManagementNotificationRepository extends JpaRepository<EventsManagementNotification,Long> {
    List<EventsManagementNotification> findByDeletedFalse();
    List<EventsManagementNotification> findByEventsManagement_EventsManagementIdAndDeletedFalse(Long eventsManagementId);
    Optional<EventsManagementNotification> findByEventsManagementNotificationIdAndDeletedFalse(Long eventsManagementNotificationId);
    Optional<EventsManagementNotification> findByDeletedFalseAndEventsManagementNotificationTypeAndEventsManagement_EventsManagementId(String eventsManagementNotificationType, Long eventsManagementId);

    @Query("select e from EventsManagementNotification e " +
            "where upper(e.eventsManagement.eventsManagementName) like upper(?1) or upper(e.eventsManagementNotificationType) like upper(?2) or upper(e.eventsManagementNotificationRecipients) like upper(?3)")
    List<EventsManagementNotification> searchByEventsManagementNameOrTypeOrRecipients(String eventsManagementName, String eventsManagementNotificationType, String eventsManagementNotificationRecipients);
}
