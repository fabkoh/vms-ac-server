package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.Event;
import com.vmsac.vmsacserver.repository.EventRepository;
import com.vmsac.vmsacserver.service.EventService;
import com.vmsac.vmsacserver.util.IPaddressWhitelisting;
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

    @PreAuthorize("permitAll()")
    @PostMapping("unicon/events")
    public ResponseEntity<?> createEvents(
            @Valid @RequestBody List<Event> ListOfEvents, HttpServletRequest request ) {
        String clientIpAddress = request.getRemoteAddr();
        if (!iPaddressWhitelisting.exisitingIPaddressVerification((clientIpAddress))){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (eventService.createEvents(ListOfEvents)){
            return  new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return  new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
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
}
