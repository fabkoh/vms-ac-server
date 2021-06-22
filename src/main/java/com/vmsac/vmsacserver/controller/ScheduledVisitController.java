package com.vmsac.vmsacserver.controller;

import com.google.zxing.WriterException;
import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.repository.ScheduledVisitRepository;
import com.vmsac.vmsacserver.service.QrCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ScheduledVisitController{

    @Autowired
    private ScheduledVisitRepository scheduledVisitRepository;

    @GetMapping(path = "/scheduled-visits")
    List<ScheduledVisit> getScheduledVisits(){
        return scheduledVisitRepository.findAll();
    }

    @PostMapping(path = "/register-scheduled-visit", consumes = "application/json")
    ResponseEntity<ScheduledVisit> createScheduledVisit(@Valid @RequestBody ScheduledVisit scheduledVisit) throws URISyntaxException, IOException, WriterException {
        ScheduledVisit registeredVisit = scheduledVisitRepository.save(scheduledVisit);
        Long qrCodeId = registeredVisit.getScheduledVisitId();
        registeredVisit.setQrCodeId(qrCodeId);
        scheduledVisitRepository.save(registeredVisit);
        QrCodeGenerator.setUpQrParams(registeredVisit);
        return ResponseEntity.created(new URI("/api/register-scheduled-visit" + registeredVisit.getScheduledVisitId())).body(registeredVisit);
    }

}
