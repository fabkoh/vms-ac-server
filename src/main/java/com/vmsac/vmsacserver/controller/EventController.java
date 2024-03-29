package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.Event;
import com.vmsac.vmsacserver.model.EventsManagement;
import com.vmsac.vmsacserver.model.notification.EmailSettings;
import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import com.vmsac.vmsacserver.model.notification.NotificationLogs;
import com.vmsac.vmsacserver.repository.EmailSettingsRepository;
import com.vmsac.vmsacserver.repository.EventRepository;
import com.vmsac.vmsacserver.repository.NotificationLogsRepository;
import com.vmsac.vmsacserver.service.EmailSettingNotificationService;
import com.vmsac.vmsacserver.service.EventService;
import com.vmsac.vmsacserver.util.IPaddressWhitelisting;
import com.vmsac.vmsacserver.service.EventsManagementNotificationService;
import com.vmsac.vmsacserver.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class EventController {
    @Autowired
    private NotificationLogsRepository notificationLogsRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private IPaddressWhitelisting iPaddressWhitelisting;

    @Autowired
    EventsManagementNotificationService eventsManagementNotificationService;
    @Autowired
    EmailSettingNotificationService emailSettingNotificationService;

    @PreAuthorize("permitAll()")
    @PostMapping("unicon/events")
    public ResponseEntity<?> createEvents(
            @Valid @RequestBody List<Event> ListOfEvents, HttpServletRequest request) {
        String clientIpAddress = request.getRemoteAddr();
        if (!iPaddressWhitelisting.exisitingIPaddressVerification((clientIpAddress))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (eventService.createEvents(ListOfEvents)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // if all saved successfully, return 200
        // else, save all excepts for errors and return 422
    }

    @GetMapping("events/count")
    public ResponseEntity<Long> countTotalEvents() {
        return new ResponseEntity<>(eventRepo.count(), HttpStatus.OK);
    }

    @GetMapping("events")
    public ResponseEntity<?> getEvents(@RequestParam(value = "batchNo", required = false) Integer batchNo,
                                       @RequestParam(value = "queryString", required = false) String queryStr,
                                       @RequestParam(value = "start", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                       @RequestParam(value = "end", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return new ResponseEntity<>(eventService.getEventsByTimeDesc(queryStr, start, end, batchNo, 500), HttpStatus.OK);

    }

    @PostMapping("events/eventsSMTP")
    public ResponseEntity<?> eventsSMTP(@RequestBody @Valid EventsManagement newChanges) throws Exception {
        Long eventsManagementId = newChanges.getEventsManagementId();
        Optional<EventsManagementNotification> eventsManagementNotification = eventsManagementNotificationService.findEmailByEventsManagementIdNotDeleted(eventsManagementId);
        if (!eventsManagementNotification.isEmpty()) {
            List<EmailSettings> emailSettings = emailSettingNotificationService.findAll();
            EmailSettings emailSettings1 = emailSettings.get(0);
            Boolean isTLS = emailSettings1.getIsTLS();
            EventsManagementNotification eventsManagementNotification1 = eventsManagementNotification.get();
            String message = eventsManagementNotification1.getEventsManagementNotificationContent();
            String emails = eventsManagementNotification1.getEventsManagementNotificationRecipients();
            String subject = eventsManagementNotification1.getEventsManagementNotificationTitle();
            String[] emailArray = emails.split(",");
            String notificationType = "email";
            List<String> emailList = Arrays.asList(emailArray);
            for (String recipents : emailList) {
                System.out.println(recipents);
                try {
                    if (isTLS) {
                        notificationService.sendSMTPTLSEmail(message, subject, recipents, emailSettings1);
                    } else {
                        notificationService.sendSMTPSSLEmail(message, subject, recipents, emailSettings1);
                    }
                } catch (Exception e) {
                    addNotif(HttpStatus.BAD_REQUEST.value(), e.getMessage(), eventsManagementNotification1, notificationType, emails);
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
            }
//            add to notif logs
            addNotif(Integer.valueOf("200"), "Success", eventsManagementNotification1, notificationType, emails);
            return new ResponseEntity<>("SMTP email sent", HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private void addNotif(int statuscode1, String e, EventsManagementNotification eventsManagementNotification1, String notificationType, String notificationRecipients) {
        System.out.println("ADD NOTIF TRIGGERED");
        Integer statuscode = statuscode1;
        String error = e;
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        NotificationLogs notificationLogs = new NotificationLogs(statuscode, error, formattedTime, eventsManagementNotification1, notificationType, notificationRecipients);
        System.out.println(notificationLogs);
        notificationLogsRepository.save(notificationLogs);
    }

    @PostMapping("events/eventsSMS")
    public ResponseEntity<?> eventsSMS(@RequestBody @Valid EventsManagement newChanges) {
        System.out.println(newChanges);
        Long eventsManagementId = newChanges.getEventsManagementId();
        Optional<EventsManagementNotification> eventsManagementNotification = eventsManagementNotificationService.findSMSByEventsManagementIdNotDeleted(eventsManagementId);
        System.out.println(eventsManagementNotification);
        if (!eventsManagementNotification.isEmpty()) {
            EventsManagementNotification eventsManagementNotification1 = eventsManagementNotification.get();
            String message = eventsManagementNotification1.getEventsManagementNotificationContent();
            String mobiles = eventsManagementNotification1.getEventsManagementNotificationRecipients();
            String[] mobileArray = mobiles.split(",");
            List<String> mobileList = Arrays.asList(mobileArray);
            String notificationType = "SMS";
            for (String mobile : mobileList) {
                System.out.println(mobile);
                System.out.println(message);
                try {
                    notificationService.sendSMS(mobile, message, notificationService);
                } catch (Exception e) {
                    addNotif(HttpStatus.BAD_REQUEST.value(), e.getMessage(), eventsManagementNotification1, notificationType, mobiles);
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
            }
            addNotif(Integer.valueOf("200"), "Success", eventsManagementNotification1, notificationType, mobiles);
            return new ResponseEntity<>("SMS sent", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("events/unauthenticated")
    public ResponseEntity<?> getUnauthenticatedScanEvents() {
        return new ResponseEntity<>(eventService.getUnauthenticatedScansIn24hrs(), HttpStatus.OK);
    }

    @GetMapping("events/unauthorised")
    public ResponseEntity<?> getUnauthorisedDoorOpenEvents() {
        return new ResponseEntity<>(eventService.getUnauthorisedDoorOpenEventsIn24hrs(), HttpStatus.OK);
    }

    @GetMapping("events/fire")
    public ResponseEntity<?> getFireEvents() {
        return new ResponseEntity<>(eventService.getFireAlarmsIn24hrs(), HttpStatus.OK);
    }
}
