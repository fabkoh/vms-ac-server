package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class VisitorController {

    @Autowired
    private VisitorRepository visitorRepository;

    @GetMapping("/visitors")
    List<Visitor> getVisitors(){
        return visitorRepository.findAll();
    }

    @GetMapping(path = "/visits-by/{idnumber}")
    private ResponseEntity<List<ScheduledVisit>> getScheduledVisitByOther(
            @PathVariable("idnumber") String idNumber) {

        return visitorRepository.findByVisitorUidWithScheduledVisits(idNumber)
                .map(v -> ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(v.getVisitorScheduledVisits()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping(path = "/register-new-visitor", consumes = "application/json")
    ResponseEntity<Visitor> createVisitor(@Valid @RequestBody Visitor newVisitor) throws URISyntaxException {
        Visitor visitor = visitorRepository.save(newVisitor);
        return ResponseEntity.created(new URI("/api/register-new-visitor/" + visitor.getVisitorId())).body(visitor);
    }
}
