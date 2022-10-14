package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.notification.EmailSettings;
import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import com.vmsac.vmsacserver.model.notification.NotificationLogs;
import com.vmsac.vmsacserver.model.notification.SmsSettings;
import com.vmsac.vmsacserver.repository.EmailSettingsRepository;
import com.vmsac.vmsacserver.repository.EventsManagementNotificationRepository;
import com.vmsac.vmsacserver.repository.NotificationLogsRepository;
import com.vmsac.vmsacserver.repository.SmsSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    SmsSettingsRepository smsSettingsRepository;

    @Autowired
    EmailSettingsRepository emailSettingsRepository;

    @Autowired
    NotificationLogsRepository notificationLogsRepository;

    @Autowired
    EventsManagementNotificationRepository eventsManagementNotificationRepository;

    public NotificationLogs save(NotificationLogs notificationLogs){
        return notificationLogsRepository.save(notificationLogs);
    }

    public SmsSettings getSmsSettings(){
        return smsSettingsRepository.findAll().get(0);
    }

    public EmailSettings getEmailSettings(){
        return emailSettingsRepository.findAll().get(0);
    }

    public Boolean changeSmsEnablement(){
        try{
            SmsSettings currentSmsSettings = smsSettingsRepository.findAll().get(0);
            currentSmsSettings.setEnabled(!currentSmsSettings.getEnabled());
            smsSettingsRepository.save(currentSmsSettings);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public Boolean changeEmailEnablement(){
        try{
            EmailSettings currentEmailSettings = emailSettingsRepository.findAll().get(0);
            currentEmailSettings.setEnabled(!currentEmailSettings.getEnabled());
            emailSettingsRepository.save(currentEmailSettings);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public SmsSettings changeSmsSettings(SmsSettings newchanges){
//        System.out.println(newchanges);
        SmsSettings currentSmsSettings = smsSettingsRepository.findAll().get(0);
        currentSmsSettings.setSmsAPI(newchanges.getSmsAPI());
//        System.out.println(currentSmsSettings);
        return smsSettingsRepository.save(currentSmsSettings);
    }

    public EmailSettings changeEmailSettings(EmailSettings newchanges){
//        System.out.println(newchanges);
        EmailSettings currentEmailSettings = emailSettingsRepository.findAll().get(0);
        currentEmailSettings.setUsername(newchanges.getUsername());
        currentEmailSettings.setEmailPassword(newchanges.getEmailPassword());
        currentEmailSettings.setEmail(newchanges.getEmail());
        currentEmailSettings.setHostAddress(newchanges.getHostAddress());
        currentEmailSettings.setPortNumber(newchanges.getPortNumber());
//        System.out.println(currentEmailSettings);
        return emailSettingsRepository.save(currentEmailSettings);
    }

    public SmsSettings smsBackToDefault(){
        SmsSettings currentSmsSettings = smsSettingsRepository.findAll().get(0);
        currentSmsSettings.setSmsAPI("BackToDefaultAPI");
        return smsSettingsRepository.save(currentSmsSettings);
    }

    public EmailSettings emailBackToDefault(){
        EmailSettings currentEmailSettings = emailSettingsRepository.findAll().get(0);
        currentEmailSettings.setUsername("DefaultName");
        currentEmailSettings.setEmailPassword("DefaultPassword");
        currentEmailSettings.setEmail("DefaultEmail");
        currentEmailSettings.setHostAddress("DefaultHostAddress");
        currentEmailSettings.setPortNumber("DefaultPortNumber");
        currentEmailSettings.setCustom(false);
        return emailSettingsRepository.save(currentEmailSettings);
    }

    public List<NotificationLogs> allNotificationLogs(){
        return notificationLogsRepository.findAll();
    }

}
