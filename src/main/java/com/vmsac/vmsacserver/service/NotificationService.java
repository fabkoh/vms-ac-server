package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.notification.EmailSettings;
import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import com.vmsac.vmsacserver.model.notification.NotificationLogs;
import com.vmsac.vmsacserver.model.notification.SmsSettings;
import com.vmsac.vmsacserver.repository.EmailSettingsRepository;
import com.vmsac.vmsacserver.repository.EventsManagementNotificationRepository;
import com.vmsac.vmsacserver.repository.NotificationLogsRepository;
import com.vmsac.vmsacserver.repository.SmsSettingsRepository;
import com.vmsac.vmsacserver.util.mapper.EmailUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.net.ssl.SSLContext;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    EmailUtil emailUtil;

    @Autowired
    SmsSettingsRepository smsSettingsRepository;

    @Autowired
    EmailSettingsRepository emailSettingsRepository;

    @Autowired
    NotificationLogsRepository notificationLogsRepository;

    @Autowired
    EventsManagementNotificationRepository eventsManagementNotificationRepository;

    public NotificationLogs save(NotificationLogs notificationLogs) {
        return notificationLogsRepository.save(notificationLogs);
    }

    public SmsSettings getSmsSettings() {
        return smsSettingsRepository.findAll().get(0);
    }

    public EmailSettings getEmailSettings() {
        return emailSettingsRepository.findAll().get(0);
    }

    public Boolean changeSmsEnablement() {
        try {
            SmsSettings currentSmsSettings = smsSettingsRepository.findAll().get(0);
            currentSmsSettings.setEnabled(!currentSmsSettings.getEnabled());
            smsSettingsRepository.save(currentSmsSettings);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void sendDefaultEmail() throws Exception {

    }

    public void sendSMTPTLSEmail(String recipient, String subject, String body, EmailSettings emailSettings) throws Exception {
        // at this point this should only be called when we want to use custom, so no need to check isCustom

        final String fromEmail = "zephan.wong@isssecurity.sg";
        final String password = "avdfhveswyonpuwq";
        final String username = emailSettings.getUsername();
        final String host = "smtp.gmail.com";
        final String port   = "587";
//        final String userPassword = emailSettings.getEmailPassword();

        System.out.println("TLSEmail Start");

        Properties TSLprops = new Properties();

// Setup mail server
        TSLprops.setProperty("mail.smtp.host", emailSettings.getHostAddress());

// mail username and password
        TSLprops.put("mail.smtp.auth", "true");
        TSLprops.put("mail.smtp.starttls.enable", "true");
        TSLprops.put("mail.debug", "true");
        TSLprops.put("mail.smtp.host", host);
        TSLprops.put("mail.smtp.port",port);
        TSLprops.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session TSLsession = Session.getInstance(TSLprops,
                new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                fromEmail, password);// Specify the Username and the PassWord
                    }
                });

        try {
            emailUtil.sendEmail(TSLsession, recipient, subject, body, fromEmail, username);
            System.out.println("TSL email sent");

//
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void sendSMTPSSLEmail(String recipient, String subject, String body, EmailSettings emailSettings) throws Exception {
        // at this point this should only be called when we want to use custom, so no need to check isCustom

        final String fromEmail = "zephan.wong@isssecurity.sg";
        final String password = "avdfhveswyonpuwq";
        final String username = emailSettings.getUsername();
        final String host = "smtp.gmail.com";
        final String port   = "465";
//        final String userPassword = emailSettings.getEmailPassword();
//        final String port   = emailSettings.getPortNumber();


        System.out.println("SSLEmail Start");

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, null, null);

        SSLContext.setDefault(sslContext);

        String[] enabledCipherSuites = { "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256" };

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sslContext.getSocketFactory());
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.ciphersuites", enabledCipherSuites);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        try {
            emailUtil.sendEmail(session, recipient, subject, body, fromEmail, username);
            System.out.println("SSL email sent");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean changeEmailEnablement() {
        try {
            EmailSettings currentEmailSettings = emailSettingsRepository.findAll().get(0);
            currentEmailSettings.setEnabled(!currentEmailSettings.getEnabled());
            emailSettingsRepository.save(currentEmailSettings);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public SmsSettings changeSmsSettings(SmsSettings newchanges) {
//        System.out.println(newchanges);
        SmsSettings currentSmsSettings = smsSettingsRepository.findAll().get(0);
        currentSmsSettings.setSmsAPI(newchanges.getSmsAPI());
//        System.out.println(currentSmsSettings);
        return smsSettingsRepository.save(currentSmsSettings);
    }

    public EmailSettings changeEmailSettings(EmailSettings newchanges) {
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

    public SmsSettings smsBackToDefault() {
        SmsSettings currentSmsSettings = smsSettingsRepository.findAll().get(0);
        currentSmsSettings.setSmsAPI("BackToDefaultAPI");
        return smsSettingsRepository.save(currentSmsSettings);
    }

    public EmailSettings emailBackToDefault() {
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

    public List<NotificationLogs> allNotificationLogs() {
        return notificationLogsRepository.findAll();
    }

    public List<NotificationLogs> getEventsByTimeDesc(int pageNo, int pageSize) {
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
