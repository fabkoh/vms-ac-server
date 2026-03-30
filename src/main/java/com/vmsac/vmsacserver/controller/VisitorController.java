package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.model.dto.VisitorRegistrationDto;
import com.vmsac.vmsacserver.repository.VisitorRepository;
import com.vmsac.vmsacserver.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

// TODO: restrict CORS origins
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class VisitorController {

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private VisitorService visitorService;

    @GetMapping("/visitors")
    List<Visitor> getVisitors() {
        return visitorRepository.findAll();
    }

    @GetMapping("/visits-by/{idnumber}")
    ResponseEntity<List<ScheduledVisit>> getScheduledVisitsByVisitor(
            @PathVariable("idnumber") String idNumber) {
        return visitorRepository.findByVisitorUid(idNumber)
                .map(v -> ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(v.getScheduledVisits()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping(path = "/register-new-visitor", consumes = "application/json")
    ResponseEntity<Visitor> createVisitor(@Valid @RequestBody Visitor newVisitor) throws URISyntaxException {
        Visitor visitor = visitorRepository.save(newVisitor);
        return ResponseEntity.created(new URI("/api/register-new-visitor/" + visitor.getVisitorId())).body(visitor);
    }

    // Permitted without auth — see WebSecurityConfig
    @GetMapping("/visitor/check")
    ResponseEntity<Visitor> checkVisitor(@RequestParam String visitorUid) {
        return visitorService.findByVisitorUid(visitorUid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Permitted without auth — see WebSecurityConfig
    @PostMapping("/visitor/register")
    ResponseEntity<?> registerVisitor(@Valid @RequestBody VisitorRegistrationDto dto) {
        try {
            visitorService.registerVisit(dto);
            return ResponseEntity.ok(Collections.singletonMap(
                    "message", "Registration successful. QR code sent to email."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Collections.singletonMap("message", e.getMessage() != null ? e.getMessage() : "Registration failed"));
        }
    }
}
