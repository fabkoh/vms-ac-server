package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.EventsManagement;
import com.vmsac.vmsacserver.model.notification.EmailSettings;
import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import com.vmsac.vmsacserver.model.notification.NotificationLogs;
import com.vmsac.vmsacserver.model.notification.SmsSettings;
import com.vmsac.vmsacserver.repository.NotificationLogsRepository;
import com.vmsac.vmsacserver.service.EventsManagementNotificationService;
import com.vmsac.vmsacserver.service.EventsManagementService;
import com.vmsac.vmsacserver.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import javax.validation.Valid;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationLogsRepository notificationLogsRepository;

    @Autowired
    EventsManagementNotificationService eventsManagementNotificationService;

    @Autowired
    EventsManagementService eventsManagementService;

    private final WebClient client = localApiClient();

    @Bean
    public WebClient localApiClient() {
        return WebClient.create("https://eserver.etlas.sg/postEmail/");
    }

    // GET sms noti settings
    @GetMapping("/notification/sms")
    public ResponseEntity<?> getSmsSettings() {
        return new ResponseEntity<>(notificationService.getSmsSettings(), HttpStatus.OK);
    }

    // GET email noti settings
    @GetMapping("/notification/email")
    public ResponseEntity<?> getEmailSettings() {
        return new ResponseEntity<>(notificationService.getEmailSettings(), HttpStatus.OK);
    }

    // flip enablement status
    @PostMapping("/notification/sms/enablement")
    public ResponseEntity<?> changeSmsEnablement() {
        if (notificationService.changeSmsEnablement()){
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // flip enablement status
    @PostMapping("/notification/email/enablement")
    public ResponseEntity<?> changeEmailEnablement() {
        if (notificationService.changeEmailEnablement()){
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // change sms api
    @PutMapping("/notification/sms")
    public ResponseEntity<?> changeSmsSettings(@RequestBody @Valid SmsSettings newChanges) {
        try{
            return new ResponseEntity<>(notificationService.changeSmsSettings(newChanges),HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // change email username, email, password, portnumber, hostAddress
    @PutMapping("/notification/email")
    public ResponseEntity<?> changeEmailSettings(@RequestBody @Valid EmailSettings newChanges) {
        try{
            return new ResponseEntity<>(notificationService.changeEmailSettings(newChanges),HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // sms back to default
    @PostMapping("/notification/sms/backToDefault")
    public ResponseEntity<?> smsBackToDefault() {
        try{
            return new ResponseEntity<>(notificationService.smsBackToDefault(),HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // email back to default
    @PostMapping("/notification/email/backToDefault")
    public ResponseEntity<?> emailBackToDefault() {
        try{
            return new ResponseEntity<>(notificationService.emailBackToDefault(),HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public static ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if(clientResponse.statusCode()!=null && (clientResponse.statusCode().is5xxServerError() || clientResponse.statusCode().is4xxClientError()) ) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            return Mono.error(new RuntimeException(errorBody + clientResponse.statusCode()));
                        });
            }else {
                return Mono.just(clientResponse);
            }
        });
    }

    @GetMapping("notification/testSMTP")
    public ResponseEntity<?> testEmail() {
        EmailSettings emailSettings = notificationService.getEmailSettings();
        if (!emailSettings.getCustom()) {
            // always return ok when using default email
            return new ResponseEntity<>("Default settings are used", HttpStatus.OK);
        }
        try {
            notificationService.sendSMTPTLSEmail("inthenetworld@yahoo.com", "Test message", "test");
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("SMTP email is valid and ready to go", HttpStatus.OK);
    }

    // all notification logs
    @GetMapping("/notification/sendEmail/{eventsManagementId}")
    public ResponseEntity<?> sendEmail(@PathVariable Long eventsManagementId) {
        // TODO: Loop through the recipients in event management when recipient addition is available
        Optional<EventsManagement> eventsManagementOptional = eventsManagementService.getEventsManagementById(eventsManagementId);
        if (eventsManagementOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        EventsManagement eventsManagement = eventsManagementOptional.get();
        Optional<EventsManagementNotification> notificationOptional = eventsManagementNotificationService.findEmailByEventsManagementIdNotDeleted(eventsManagement.getEventsManagementId());
        if (notificationOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        EventsManagementNotification notification = notificationOptional.get();
        EmailSettings emailSettings = notificationService.getEmailSettings();
        if (!emailSettings.getEnabled()) {
            NotificationLogs notificationLogs = new NotificationLogs(null, 400, "Email is disabled", DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
                    .withZone(ZoneId.of("GMT+08:00"))
                    .format(Instant.now()),notification);
            notificationService.save(notificationLogs);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (emailSettings.getCustom()) {
            try {
                // only send to the first one
                notificationService.sendSMTPTLSEmail(notification.getEventsManagementNotificationRecipients().split(",")[0], notification.getEventsManagementNotificationTitle(), notification.getEventsManagementNotificationContent());
                // notificationService.sendSMTPTLSEmail(notification.getEventsManagementNotificationRecipients().split(",")[0], notification.getEventsManagementNotificationTitle(), notification.getEventsManagementNotificationContent());
            } catch (Exception e) {
                NotificationLogs notificationLogs = new NotificationLogs(null, 400, e.getMessage(), DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
                        .withZone(ZoneId.of("GMT+08:00"))
                        .format(Instant.now()),notification);
                notificationService.save(notificationLogs);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        String uri = String.format("%s/%s", notification.getEventsManagementNotificationTitle(), notification.getEventsManagementNotificationContent());
        AtomicBoolean hasError = new AtomicBoolean(false);
        NotificationLogs notificationLogs;
        try {
            String clientResponse = client.get().uri(uri).retrieve()
                    .onStatus(
                            HttpStatus.INTERNAL_SERVER_ERROR::equals,
                            response -> {
                                return response.bodyToMono(String.class).map(Exception::new);
                            })
                    .onStatus(
                            HttpStatus.BAD_REQUEST::equals,
                            response -> {
                                return response.bodyToMono(String.class).map(Exception::new);
                            })
                    .bodyToMono(String.class)
                    .block(Duration.ofMillis(5000));
            if (!hasError.get()) {
                // saving the time in UTC
                notificationLogs = new NotificationLogs(null, 200, "", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmX")
                        .withZone(ZoneOffset.UTC)
                        .format(Instant.now()),notification);
                notificationService.save(notificationLogs);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (Exception e) {
            // do nothing
        }
        notificationLogs = new NotificationLogs(null, 400, "Email failed to send", DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
                .withZone(ZoneId.of("GMT+08:00"))
                .format(Instant.now()),notification);
        notificationService.save(notificationLogs);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // all notification logs
    @GetMapping("/notification/logs/all")
    public ResponseEntity<?> AllNotificationLogs(@RequestParam(value = "batchNo", required = false) Integer batchNo,
                                                 @RequestParam(value = "queryString", required = false) String queryStr,
                                                 @RequestParam(value = "start", required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                 @RequestParam(value = "end", required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try{
            return new ResponseEntity<>(notificationService.allNotificationLogs(),HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/notification/logs/count")
    public ResponseEntity<Long> countTotalEvents() {
        return new ResponseEntity<>(notificationLogsRepository.count(), HttpStatus.OK);
    }

}
