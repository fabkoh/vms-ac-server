package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.PersonDto;
import com.vmsac.vmsacserver.model.notification.EmailSettings;
import com.vmsac.vmsacserver.model.notification.NotificationLogs;
import com.vmsac.vmsacserver.model.notification.SmsSettings;
import com.vmsac.vmsacserver.repository.NotificationLogsRepository;
import com.vmsac.vmsacserver.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

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

    // all notification logs
    @GetMapping("/notification/logs/all")
    public ResponseEntity<?> AllNotificationLogs() {
        try{
            return new ResponseEntity<>(notificationService.allNotificationLogs(),HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
