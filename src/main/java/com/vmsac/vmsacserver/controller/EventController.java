package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.Event;
import com.vmsac.vmsacserver.model.EventsManagement;
import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import com.vmsac.vmsacserver.repository.EventRepository;
import com.vmsac.vmsacserver.service.EventService;
import com.vmsac.vmsacserver.util.IPaddressWhitelisting;
import com.vmsac.vmsacserver.service.EventsManagementNotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private IPaddressWhitelisting iPaddressWhitelisting;

    @Autowired
    EventsManagementNotificationService eventsManagementNotificationService;

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
    public ResponseEntity<?> eventsSMTP(@RequestBody @Valid EventsManagement newChanges) {
        Long eventsManagementId = newChanges.getEventsManagementId();
        Optional<EventsManagementNotification> eventsManagementNotification = eventsManagementNotificationService.findEmailByEventsManagementIdNotDeleted(eventsManagementId);
        System.out.println(newChanges);
        if (!eventsManagementNotification.isEmpty()) {
            String message = eventsManagementNotification.get().getEventsManagementNotificationContent();
            String recipents = eventsManagementNotification.get().getEventsManagementNotificationRecipients();
        }
//        if (!newChanges.getCustom()) {
//            // always return ok when using default email
//            return new ResponseEntity<>("Default settings are used", HttpStatus.OK);
//        }
//        try {
//            if (newChanges.getIsTLS()) {
//                notificationService.sendSMTPTLSEmail(newChanges.getEmail(), newChanges.getRecipentUser(), newChanges.getRecipentEmail(), newChanges);
//            } else {
//                notificationService.sendSMTPSSLEmail(newChanges.getEmail(), newChanges.getRecipentUser(), newChanges.getRecipentEmail(), newChanges);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
        return new ResponseEntity<>("SMTP email is valid and ready to go", HttpStatus.OK);
    }

    @PostMapping("events/eventsSMS")
    public ResponseEntity<?> eventsSMS(@RequestBody @Valid EventsManagement newChanges) {
        System.out.println(newChanges);
//        if (!newChanges.getCustom()) {
//            // always return ok when using default email
//            return new ResponseEntity<>("Default settings are used", HttpStatus.OK);
//        }
//        try {
//            if (newChanges.getIsTLS()) {
//                notificationService.sendSMTPTLSEmail(newChanges.getEmail(), newChanges.getRecipentUser(), newChanges.getRecipentEmail(), newChanges);
//            } else {
//                notificationService.sendSMTPSSLEmail(newChanges.getEmail(), newChanges.getRecipentUser(), newChanges.getRecipentEmail(), newChanges);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
        return new ResponseEntity<>("SMTP email is valid and ready to go", HttpStatus.OK);
    }
}
