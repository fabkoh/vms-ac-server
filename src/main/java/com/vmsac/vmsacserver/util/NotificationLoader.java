package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.notification.EmailSettings;
import com.vmsac.vmsacserver.model.notification.NotificationLogs;
import com.vmsac.vmsacserver.model.notification.SmsSettings;
import com.vmsac.vmsacserver.repository.EmailSettingsRepository;
import com.vmsac.vmsacserver.repository.EventsManagementNotificationRepository;
import com.vmsac.vmsacserver.repository.NotificationLogsRepository;
import com.vmsac.vmsacserver.repository.SmsSettingsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "devpostgres"})
public class NotificationLoader implements CommandLineRunner {

    private final NotificationLogsRepository notificationLogsRepository;
    private final SmsSettingsRepository smsSettingsRepository;
    private final EmailSettingsRepository emailSettingsRepository;
    private final EventsManagementNotificationRepository eventsManagementNotificationRepository;

    public NotificationLoader(NotificationLogsRepository notificationLogsRepository, SmsSettingsRepository smsSettingsRepository, EmailSettingsRepository emailSettingsRepository, EventsManagementNotificationRepository eventsManagementNotificationRepository) {
        this.notificationLogsRepository = notificationLogsRepository;
        this.smsSettingsRepository = smsSettingsRepository;
        this.emailSettingsRepository = emailSettingsRepository;
        this.eventsManagementNotificationRepository = eventsManagementNotificationRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (smsSettingsRepository.findAll().isEmpty()) loadData();
    }

    private void loadData(){
        smsSettingsRepository.save(SmsSettings.builder()
                .smsAPI("ThisIsTheTestingApi")
                .enabled(true)
                .build());

        emailSettingsRepository.save(EmailSettings.builder()
                .username("Lee Yong Ning")
                .email("yongning.lee@iss.security.sg")
                .emailPassword("TestingPassword")
                .hostAddress("TestingHostAddress")
                .portNumber("8080")
                .enabled(true)
                .custom(true)
                .build());

        notificationLogsRepository.save(NotificationLogs.builder()
                .notificationLogsStatusCode(200)
                .notificationLogsError("TestingError")
                .timeSent("TestTime")
                .build());
    }



}
