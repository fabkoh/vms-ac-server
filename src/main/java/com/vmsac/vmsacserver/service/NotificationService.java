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
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
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
        SmsSettings smsSettings = smsSettingsRepository.findAll().get(0);
        System.out.println(smsSettings);
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

    public void sendSMTPTLSEmail(String text, String emailSubject, String recipentEmail, EmailSettings emailSettings) throws Exception {
        // at this point this should only be called when we want to use custom, so no need to check isCustom
//        check if email settings is enabled


        emailUtil.TLSEmail(recipentEmail, emailSubject, text,
                emailSettings);
    }

    public void sendSMTPSSLEmail(String text, String emailSubject, String recipentEmail, EmailSettings emailSettings) throws Exception {
        // at this point this should only be called when we want to use custom, so no need to check isCustom
//        EmailSettings currentEmailSettings = emailSettingsRepository.findAll().get(0);
//        if(!currentEmailSettings.getEnabled()){
//            throw new RuntimeException();
//        }


        emailUtil.SSLEmail(recipentEmail, emailSubject, text,
                emailSettings);

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
        System.out.println(newchanges);
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
        currentEmailSettings.setUsername("Etlas");
        currentEmailSettings.setEmailPassword("yOw6$teqE");
        currentEmailSettings.setEmail("notifications@etlas.sg");
        currentEmailSettings.setHostAddress("smtp.zoho.com");
        currentEmailSettings.setPortNumber("587");
        currentEmailSettings.setIsTLS(true);
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

            List<NotificationLogs> notifications2 = notificationLogsRepository.searchByNotificationType(queryString);
            List<String> notificationsTypes = notifications2.stream().map(NotificationLogs::getNotificationType).collect(Collectors.toList());

            List<NotificationLogs> notifications3 = notificationLogsRepository.searchByNotificationRecipients(queryString);
            List<String> notificationsRecipents = notifications3.stream().map(NotificationLogs::getNotificationRecipients).collect(Collectors.toList());


            Timestamp startTimestamp = Objects.isNull(start) ? Timestamp.valueOf("1970-01-01 00:00:00") : Timestamp.valueOf(start);
            Timestamp endTimestamp = Objects.isNull(end) ? Timestamp.valueOf("2100-01-01 00:00:00") : Timestamp.valueOf(end);
            System.out.println("start is " + startTimestamp);
            System.out.println("end is " + endTimestamp);
            if (notificationsIds == null || notificationsIds.size() == 0) {
                result = notificationLogsRepository.findByTime(startTimestamp, endTimestamp, PageRequest.of(pageNo, pageSize));
            } else {
            result = notificationLogsRepository.findByQueryString(notificationsIds,notificationsTypes,notificationsRecipents,
                        startTimestamp, endTimestamp
//                    ,PageRequest.of(pageNo, pageSize)
            );
            }
        } else
            result = getEventsByTimeDesc(pageNo, pageSize);

        System.out.println("results is "+ result);
        return result;
    }

    public static void sendSMS(
            String mobilenumber,
            String message,
            NotificationService notificationService) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestTemplate restTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(HttpClientBuilder.create().setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build()).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build());
        restTemplate.setRequestFactory(requestFactory);

        SmsSettings smsSettings = notificationService.getSmsSettings();
        if (!smsSettings.getEnabled()) {
            throw new RuntimeException("SMS settings are not enabled");
        }
        // use smsSettings object here

        String apikey = smsSettings.getSmsAPI();
        System.out.println(apikey);
        System.out.println(message);
        String url = "https://api.inthenetworld.com/sms/send/{apikey}/{mobilenumber}/{message}";
        try {
            String responseBody = restTemplate.getForObject(url, String.class, apikey, mobilenumber, message);
            System.out.println(responseBody);
        } catch (
                Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String getSmsCredits() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestTemplate restTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(HttpClientBuilder.create().setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build()).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build());
        restTemplate.setRequestFactory(requestFactory);

        String url = "https://api.inthenetworld.com/sms/getQuota/isssecurity";
//        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String responseBody = restTemplate.getForObject(url, String.class);
        System.out.println(responseBody);
        return responseBody;
    }


}
