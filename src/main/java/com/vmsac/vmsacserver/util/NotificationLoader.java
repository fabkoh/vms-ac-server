package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.notification.EmailSettings;
import com.vmsac.vmsacserver.model.notification.NotificationLogs;
import com.vmsac.vmsacserver.model.notification.SmsSettings;
import com.vmsac.vmsacserver.model.videorecorder.VideoRecorder;
import com.vmsac.vmsacserver.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile({"dev", "devpostgres"})
public class NotificationLoader implements CommandLineRunner {

    private final NotificationLogsRepository notificationLogsRepository;
    private final SmsSettingsRepository smsSettingsRepository;
    private final EmailSettingsRepository emailSettingsRepository;
    private final EventsManagementNotificationRepository eventsManagementNotificationRepository;
    private final VideoRecorderRepository videoRecorderRepository;

    public NotificationLoader(NotificationLogsRepository notificationLogsRepository, SmsSettingsRepository smsSettingsRepository, EmailSettingsRepository emailSettingsRepository, EventsManagementNotificationRepository eventsManagementNotificationRepository, VideoRecorderRepository videoRecorderRepository) {
        this.notificationLogsRepository = notificationLogsRepository;
        this.smsSettingsRepository = smsSettingsRepository;
        this.emailSettingsRepository = emailSettingsRepository;
        this.eventsManagementNotificationRepository = eventsManagementNotificationRepository;
        this.videoRecorderRepository = videoRecorderRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (smsSettingsRepository.findAll().isEmpty()) loadData();
    }

    private void loadData(){
        smsSettingsRepository.save(SmsSettings.builder()
                .smsAPI("isssecurity")
                .enabled(true)
                .build());

        emailSettingsRepository.save(EmailSettings.builder()
                .username("Zephan Wong Kai En")
                .email("zephan.wong@isssecurity.sg")
                .emailPassword("avdfhveswyonpuwq")
                .hostAddress("smtp.gmail.com")
                .portNumber("587")
                .enabled(true)
                .custom(true)
                .isTLS(true)
                .build());

        notificationLogsRepository.save(NotificationLogs.builder()
                .notificationLogsStatusCode(400)
                .notificationLogsError("TestingError")
                .timeSent("10-22-2022 03:50:39")
                .build());

        videoRecorderRepository.save(VideoRecorder.builder()
                .recorderIWSPort(7681)
                .recorderName("testing")
                .recorderPassword("ISSNVRTest01")
                .recorderPortNumber(8085)
                .recorderPrivateIp("192.168.1.172")
                .recorderPublicIp("118.201.255.164")
                .recorderSerialNumber("DS-7616NI-I21620210923CCRRG74241239WCVU")
                .recorderUsername("admin")
                .deleted(false)
                .created(LocalDateTime.now())
                .build());
    }



}
