package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.notification.EmailSettings;
import com.vmsac.vmsacserver.repository.EmailSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailSettingNotificationService {
    @Autowired
    private EmailSettingsRepository emailSettingsRepository;

    public List<EmailSettings> findAll() {
        return emailSettingsRepository.findAll();
    }
}

//    public List<EventsManagementNotification> findByEventsManagementIdNotDeleted(Long eventsManagementId) {
//        return eventsManagementNotificationRepository.findByEventsManagement_EventsManagementIdAndDeletedFalse(eventsManagementId);
//    }
//
//    public Optional<EventsManagementNotification> findEmailByEventsManagementIdNotDeleted(Long eventsManagementId) {
//        return eventsManagementNotificationRepository.findByDeletedFalseAndEventsManagementNotificationTypeAndEventsManagement_EventsManagementId("EMAIL", eventsManagementId);
//    }
//
//    public Optional<EventsManagementNotification> findSMSByEventsManagementIdNotDeleted(Long eventsManagementId) {
//        return eventsManagementNotificationRepository.findByDeletedFalseAndEventsManagementNotificationTypeAndEventsManagement_EventsManagementId("SMS", eventsManagementId);
//    }
//
//    public Optional<EventsManagementNotification> findByIdNotDeleted(Long Id){
//        return eventsManagementNotificationRepository.findByEventsManagementNotificationIdAndDeletedFalse(Id);
//    }
//
//    public EventsManagementNotification save(EventsManagementNotification eventManagementNotification){
//        return eventsManagementNotificationRepository.save(eventManagementNotification);
//    }
//
//    public void delete(Long id){
//        EventsManagementNotification deleted = eventsManagementNotificationRepository.findByEventsManagementNotificationIdAndDeletedFalse(id)
//                .orElseThrow(() -> new RuntimeException("Event Management Notification with id "+ id + " does not exist"));
//        deleted.setDeleted(true);
//
//        eventsManagementNotificationRepository.save(deleted);
//    }

