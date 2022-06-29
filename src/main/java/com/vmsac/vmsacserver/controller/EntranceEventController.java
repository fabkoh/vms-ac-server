package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.EntranceEventDto;
import com.vmsac.vmsacserver.service.EntranceEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class EntranceEventController {

    @Autowired
    private EntranceEventService entranceEventService;

    @PostMapping("unicon/events")
    public ResponseEntity<?> createEvents(
            @Valid @RequestBody List<EntranceEventDto> ListOfEvents ) {

        if (entranceEventService.createEvents(ListOfEvents)){
            return  new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return  new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // if all saved successfully, return 200
        // else, save all excepts for errors and return 422
    }

    @GetMapping("events")
    public ResponseEntity<?> getEvents() {

        return new ResponseEntity<>(entranceEventService.getAllEvents(), HttpStatus.OK);
    }
}
