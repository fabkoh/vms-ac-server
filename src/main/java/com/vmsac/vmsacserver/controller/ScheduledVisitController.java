package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.repository.ScheduledVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;

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
    ResponseEntity<ScheduledVisit> createScheduledVisit(@Valid @RequestBody ScheduledVisit scheduledVisit) throws URISyntaxException{
        ScheduledVisit registeredVisit = scheduledVisitRepository.save(scheduledVisit);
        return ResponseEntity.created(new URI("/api/register-scheduled-visit" + registeredVisit.getScheduledVisitId())).body(registeredVisit);

    }

}
