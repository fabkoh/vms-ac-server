package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.EntranceEventDto;
import com.vmsac.vmsacserver.model.EntranceEventType;
import com.vmsac.vmsacserver.model.EntranceEventTypeCreateDto;
import com.vmsac.vmsacserver.repository.EntranceEventRepository;
import com.vmsac.vmsacserver.repository.EntranceEventTypeRepository;
import com.vmsac.vmsacserver.service.EntranceEventService;
import com.vmsac.vmsacserver.service.EntranceEventTypeServie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/entrance")
public class EntranceEventController {

    @Autowired
    private EntranceEventRepository eventRepository;
    @Autowired
    private EntranceEventService entranceEventService;

    @Autowired
    private EntranceEventTypeRepository typeRepository;

    @Autowired
    private EntranceEventTypeServie typeServie;

    @PostMapping("/event/type")
    public ResponseEntity<?> createEventType(@RequestBody EntranceEventTypeCreateDto dto) {

        // create and save the new event type
        EntranceEventType newType = typeServie.createType(dto);

        // return the just created event type with status code 201
        return new ResponseEntity<>(newType, HttpStatus.CREATED);
    }

    @GetMapping("/event/types")
    public ResponseEntity<?> getAllEventTypes() {

        // does not use Dto here
        return new ResponseEntity<>(typeRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/event/type/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        Optional<EntranceEventType> type = typeRepository.findById(id);
        if (type.isPresent())
            return new ResponseEntity<>(type, HttpStatus.OK);
        else
            return ResponseEntity.notFound().build();
    }

    @PatchMapping("/event/type/{id}")
    public ResponseEntity<?> updateEventType(@PathVariable Long id,
                                             @RequestBody Map<String, Object> fields) {
        // does not save if not found
        Optional<EntranceEventType> typeOptional = typeRepository.findById(id);
        if (typeOptional.isPresent()) {
            EntranceEventType type = typeOptional.get();
            fields.forEach((k, v) -> {
                // use reflection to get field k and set it to value v
                Field field = ReflectionUtils.findField(EntranceEventType.class, k);
                field.setAccessible(true);
                ReflectionUtils.setField(field, type, v);
            });
            typeRepository.save(type);
            return ResponseEntity.ok().build();
        }
        else return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/event/type/{id}")
    public ResponseEntity<?> deleteEventTypeById(@PathVariable Long id) {
        // delete if this id exists, do nothing otherwise, always return status code 200
        typeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvents(
            @Valid @RequestBody List<EntranceEventDto> ListOfEvents ) {

        if (entranceEventService.createEvents(ListOfEvents)){
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // if all saved successfully, return 200
        // else, save all excepts for errors and return 422
    }

    @GetMapping("/events")
    public ResponseEntity getEvents() {
        return new ResponseEntity(eventRepository.findAll(), HttpStatus.OK);
        //return new ResponseEntity(entranceEventService.getAllEvents(), HttpStatus.OK);
    }

    @DeleteMapping("/event/{id}")
    public ResponseEntity deleteEventById(@PathVariable Long id) {
        // delete if this id exists, do nothing otherwise, always return status code 200
        eventRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
