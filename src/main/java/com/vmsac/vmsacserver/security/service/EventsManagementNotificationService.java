package com.vmsac.vmsacserver.security.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import com.vmsac.vmsacserver.repository.EventsManagementNotificationRepository;

@Service
public class EventsManagementNotificationService {
    @Autowired
    EventsManagementNotificationRepository eventsManagementNotificationRepository;

    public List<EventsManagementNotification> findAllNotDeleted() {
        return eventsManagementNotificationRepository.findByDeletedFalse();
    }

    public List<EventsManagementNotification> findByEventsManagementIdNotDeleted(Long eventsManagementId) {
        return eventsManagementNotificationRepository.findByEventsManagement_EventsManagementIdAndDeletedFalse(eventsManagementId);
    }

    public Optional<EventsManagementNotification> findEmailByEventsManagementIdNotDeleted(Long eventsManagementId) {
        return eventsManagementNotificationRepository.findByDeletedFalseAndEventsManagementNotificationTypeAndEventsManagement_EventsManagementId("EMAIL", eventsManagementId);
    }

    public Optional<EventsManagementNotification> findSMSByEventsManagementIdNotDeleted(Long eventsManagementId) {
        return eventsManagementNotificationRepository.findByDeletedFalseAndEventsManagementNotificationTypeAndEventsManagement_EventsManagementId("SMS", eventsManagementId);
    }

    public Optional<EventsManagementNotification> findByIdNotDeleted(Long Id){
        return eventsManagementNotificationRepository.findByEventsManagementNotificationIdAndDeletedFalse(Id);
    }

    public EventsManagementNotification save(EventsManagementNotification eventManagementNotification){
        return eventsManagementNotificationRepository.save(eventManagementNotification);
    }

    public void delete(Long id){
        EventsManagementNotification deleted = eventsManagementNotificationRepository.findByEventsManagementNotificationIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Event Management Notification with id "+ id + " does not exist"));
        deleted.setDeleted(true);

        eventsManagementNotificationRepository.save(deleted);
    }
}
