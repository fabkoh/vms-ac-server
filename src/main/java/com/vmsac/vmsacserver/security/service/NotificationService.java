package com.vmsac.vmsacserver.security.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.notification.EmailSettings;
import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import com.vmsac.vmsacserver.model.notification.NotificationLogs;
import com.vmsac.vmsacserver.model.notification.SmsSettings;
import com.vmsac.vmsacserver.repository.EmailSettingsRepository;
import com.vmsac.vmsacserver.repository.EventsManagementNotificationRepository;
import com.vmsac.vmsacserver.repository.NotificationLogsRepository;
import com.vmsac.vmsacserver.repository.SmsSettingsRepository;
import com.vmsac.vmsacserver.util.EmailUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
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

    public void sendDefaultEmail() throws Exception{

    }

    public void sendSMTPTLSEmail(String recipient, String subject, String body, EmailSettings emailSettings) throws Exception {
        // at this point this should only be called when we want to use custom, so no need to check isCustom

        final String fromEmail = emailSettings.getEmail();
        final String password = emailSettings.getEmailPassword();

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", emailSettings.getHostAddress()); //SMTP Host
        props.put("mail.smtp.port", emailSettings.getPortNumber()); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);
        try {
            EmailUtil.sendEmail(session, recipient,subject, body);
        }
        catch (Exception e) {
            throw e;
        }
    }

    public void sendSMTPSSLEmail(String recipient, String subject, String body, EmailSettings emailSettings) throws Exception {
        // at this point this should only be called when we want to use custom, so no need to check isCustom

        final String fromEmail = emailSettings.getEmail();
        final String password = emailSettings.getEmailPassword();

        System.out.println("SSLEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", emailSettings.getHostAddress()); //SMTP Host
        // props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
        props.put("mail.smtp.socketFactory.port", emailSettings.getPortNumber());
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
        props.put("mail.smtp.port", emailSettings.getPortNumber()); //SMTP Port

        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getInstance(props, auth);
        System.out.println("Session created");
        try {
            EmailUtil.sendEmail(session, recipient,subject, body);
        }
        catch (Exception e) {
            throw e;
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
        currentEmailSettings.setCustom(true);
        currentEmailSettings.setIsTLS(newchanges.getIsTLS());
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
        currentEmailSettings.setIsTLS(false);
        currentEmailSettings.setCustom(false);
        return emailSettingsRepository.save(currentEmailSettings);
    }

    public List<NotificationLogs> allNotificationLogs(){
        return notificationLogsRepository.findAll();
    }

    public List<NotificationLogs> getEventsByTimeDesc(int pageNo, int pageSize){
        List<NotificationLogs> allLogs = notificationLogsRepository.findByOrderByTimeSentDesc(PageRequest.of(pageNo, pageSize));
        return allLogs;
    }


    public List<NotificationLogs> getNotificationLogsByTimeDesc(String queryString, LocalDateTime start, LocalDateTime end, int pageNo, int pageSize) {
        List<NotificationLogs> result;
        if ((queryString != null && !queryString.equals("")) || !Objects.isNull(start) || !Objects.isNull(end)) {
            List<EventsManagementNotification> notifications = eventsManagementNotificationRepository.searchByEventsManagementNameOrTypeOrRecipients(queryString, queryString, queryString);
            List<Long> notificationsIds = notifications.stream().map(EventsManagementNotification::getEventsManagementNotificationId).collect(Collectors.toList());
            Timestamp startTimestamp = Objects.isNull(start) ? Timestamp.valueOf("1970-01-01 00:00:00") : Timestamp.valueOf(start);
            Timestamp endTimestamp = Objects.isNull(end) ? Timestamp.valueOf("2050-01-01 00:00:00") : Timestamp.valueOf(end);
            if (notificationsIds == null || notificationsIds.size() == 0) {
                result = notificationLogsRepository.findByTime(startTimestamp, endTimestamp, PageRequest.of(pageNo, pageSize));
            } else {
                result = notificationLogsRepository.findByQueryString(notificationsIds,
                        startTimestamp, endTimestamp, PageRequest.of(pageNo, pageSize));
            }
        } else
            result = getEventsByTimeDesc(pageNo, pageSize);

        return result;
    }

}
