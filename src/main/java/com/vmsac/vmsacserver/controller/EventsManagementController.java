package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.repository.*;
import com.vmsac.vmsacserver.service.AuthDeviceService;
import com.vmsac.vmsacserver.service.ControllerService;
import com.vmsac.vmsacserver.service.EventService;
import com.vmsac.vmsacserver.service.EventsManagementService;
import com.vmsac.vmsacserver.service.TriggerSchedulesService;
import com.vmsac.vmsacserver.util.DateTimeParser;
import com.vmsac.vmsacserver.util.FieldsModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@Validated
@RequestMapping("/api")
public class EventsManagementController {

    @Autowired
    private ControllerService controllerService;

    @Autowired
    private AuthDeviceService authDeviceService;

    @Autowired
    EventActionInputTypeRepository inputTypeRepository;

    @Autowired
    EventsManagementRepository eventsManagementRepository;

    @Autowired
    EventsManagementService eventsManagementService;

    @Autowired
    TriggerSchedulesRepository triggerSchedulesRepository;

    @Autowired
    TriggerSchedulesService triggerSchedulesService;

    @Autowired
    EntranceRepository entranceRepository;

    @Autowired
    ControllerRepository controllerRepository;

    @Autowired
    InputEventRepository inputEventRepository;

    @Autowired
    EventActionOutputTypeRepository outputTypeRepository;

    @Autowired
    OutputEventRepository outputEventRepository;

    @Autowired
    EventService eventService;

    @Autowired
    FieldsModifier fieldsModifier;

    @Autowired
    DateTimeParser dateTimeParser;

    // POST EventActionInputType
    @PostMapping("/event/input/type")
    public ResponseEntity<?> postInputType(@RequestBody @Valid EventActionInputType dto) {
        EventActionInputType inputType = inputTypeRepository.save(
                new EventActionInputType(null, dto.getEventActionInputName(),
                        dto.getTimerEnabled(), dto.getEventActionInputConfig())
        );
        return new ResponseEntity<>(inputType, HttpStatus.CREATED);
    }

    // GET ALL EventActionInputType
    @GetMapping("/event/input/types")
    public ResponseEntity<?> getAllInputTypes() {
        return new ResponseEntity<>(inputTypeRepository.findAll(), HttpStatus.OK);
    }

    // PUT EventActionInputType
    @PutMapping("/event/input/type/{id}")
    public ResponseEntity<?> putInputType(@RequestBody @Valid EventActionInputType dto,
                                          @PathVariable Long id) {

        if (inputTypeRepository.existsById(dto.getEventActionInputId())) {
            EventActionInputType type = inputTypeRepository.save(dto);
            return new ResponseEntity<>(type, HttpStatus.OK);
        }
        else return ResponseEntity.notFound().build();
    }

    // DELETE EventActionInputType
    @DeleteMapping("/event/input/type/{id}")
    public ResponseEntity<?> deleteInputType(@PathVariable Long id) {
        if (inputTypeRepository.existsById(id)) {
            inputTypeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // PUT InputEvent
    @PutMapping("/event/input/{id}")
    public ResponseEntity<?> putInputEvent(@RequestBody @Valid InputEvent dto,
                                           @PathVariable Long id) {
        if (inputEventRepository.existsById(id)) {
            if (inputTypeRepository.existsById(dto.getEventActionInputType().getEventActionInputId())) {
                return new ResponseEntity<>(inputEventRepository.save(dto), HttpStatus.OK);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // POST EventActionOutputType
    @PostMapping("event/output/type")
    public ResponseEntity<?> postOutputType(@RequestBody @Valid EventActionOutputType dto) {
        EventActionOutputType saved = outputTypeRepository.save(
                new EventActionOutputType(null, dto.getEventActionOutputName(),
                        dto.getTimerEnabled(), dto.getEventActionOutputConfig())
        );
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // GET ALL EventActionOutputType
    @GetMapping("/event/output/types")
    public ResponseEntity<?> getAllOutputTypes() {
        return new ResponseEntity<>(outputTypeRepository.findAll(), HttpStatus.OK);
    }

    // PUT EventActionOutputType
    @PutMapping("event/output/type/{id}")
    public ResponseEntity<?> putOutputType(@RequestBody @Valid EventActionOutputType dto,
                                           @PathVariable Long id) {

        if(outputTypeRepository.existsById(dto.getEventActionOutputId())) {
            EventActionOutputType type = outputTypeRepository.save(dto);
            return new ResponseEntity<>(type, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE EventActionOutputType
    @DeleteMapping("event/output/type/{id}")
    public ResponseEntity<?> deleteOutputType(@PathVariable Long id) {

        if (outputTypeRepository.existsById(id)) {
            outputTypeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // PUT OutputEvent
    @PutMapping("event/output/{id}")
    public ResponseEntity<?> putOutputEvent(@RequestBody @Valid OutputEvent dto,
                                            @PathVariable Long id) {
        if (outputEventRepository.existsById(dto.getOutputEventId())) {
            if(outputTypeRepository.existsById(dto.getEventActionOutputType().getEventActionOutputId()))
                return new ResponseEntity<>(outputEventRepository.save(dto), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // POST EventsManagement
    @PostMapping("eventsmanagement")
    public ResponseEntity<?> postEventsMangement(@RequestBody @Valid EventsManagementCreateDto dto) {

        List<EventsManagement> eventsManagements = eventsManagementService.create(dto);

        if (eventsManagements.size() > 0)
            return new ResponseEntity<>(eventsManagements.stream().map(em -> {return eventsManagementService.toDto(em);})
                    .collect(Collectors.toList()), HttpStatus.CREATED);
        else return ResponseEntity.badRequest().build();
    }

    // GET EventsManagement
    @GetMapping("eventsmanagements")
    public ResponseEntity<?> getAllEventsMangement() {
        return new ResponseEntity<>(eventsManagementRepository.findAllByDeletedFalse()
                .stream().map(em -> {return eventsManagementService.toDto(em);})
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    // GET EventsManagement Entrance
    @GetMapping("eventsmanagements/entrance/{entranceId}")
    public ResponseEntity<?> getAllEventsMangementForEntrance(@PathVariable Long entranceId) {
        if (entranceRepository.existsByEntranceId(entranceId))
            return new ResponseEntity<>(entranceRepository.findByEntranceIdAndDeletedFalse(entranceId)
                .get().getEventsManagements()
                .stream().map(em -> eventsManagementService.toDto(em))
                .collect(Collectors.toList()), HttpStatus.OK);

        else return ResponseEntity.notFound().build();
    }

    // GET EventsManagement Controller
    @GetMapping("eventsmanagements/controller/{controllerId}")
    public ResponseEntity<?> getAllEventsMangementForController(@PathVariable Long controllerId) {
        Optional<Controller> optionalController = controllerService.findById(controllerId);
        if (optionalController.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<AuthDevice> authdevicelist = authDeviceService.findbyControllerId(controllerId);
        List<EventsManagement> eventManagements = optionalController.get().getEventsManagements();

        authdevicelist.forEach(authdevice-> {
            if (authdevice.getEntrance() != null){
                authdevice.getEntrance().getEventsManagements().forEach(em -> {
                    if (!eventManagements.contains(em)) eventManagements.add(em);
                });
            }
        });
        return new ResponseEntity<>(eventManagements
                .stream().map(em -> eventsManagementService.toDto(em))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    // PUT EventsManagement
    @PutMapping("eventsmanagement/{emId}")
    public ResponseEntity<?> putEventsMangement(@RequestBody @Valid EventsManagement dto,
                                                @PathVariable Long emId) {
        if (eventsManagementRepository.existsByDeletedFalseAndEventsManagementId(emId)) {
            EventsManagement em = eventsManagementRepository.findByDeletedFalseAndEventsManagementId(emId).get();
            // set the attributes that were ignored in JSON
            dto.setDeleted(em.getDeleted());
            dto.setController(em.getController());
            dto.setEntrance(em.getEntrance());
            return new ResponseEntity<>(eventsManagementService.toDto(eventsManagementRepository.save(dto)), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // Replace all eventsmanagement with newly created ones
    @PutMapping("eventsmanagement/replace")
    public ResponseEntity<?> replaceEventsMangement(@RequestBody @Valid List<EventsManagementCreateDto> dtos,
                                                    @RequestParam("controllerIds") List<Integer> controllerIds,
                                                    @RequestParam("entranceIds") List<Integer> entranceIds) {
        for (Integer controllerId : controllerIds) {
            Optional<Controller> opCon = controllerRepository.findByControllerIdEqualsAndDeletedFalse(controllerId.longValue());
            if (opCon.isPresent())
                for (EventsManagement em : opCon.get().getEventsManagements()) {
                    eventsManagementService.deleteById(em.getEventsManagementId());
                }
        }

        for (Integer entranceId : entranceIds) {
            Optional<Entrance> opEnt = entranceRepository.findByEntranceIdAndDeletedFalse(entranceId.longValue());
            if (opEnt.isPresent())
                for (EventsManagement em : opEnt.get().getEventsManagements()) {
                    eventsManagementService.deleteById(em.getEventsManagementId());
                }
        }

        List<EventsManagement> eventsManagements = new ArrayList<>();

        for (EventsManagementCreateDto dto : dtos) {
            dto.setControllerIds(controllerIds);
            dto.setEntranceIds(entranceIds);
            eventsManagements.addAll(eventsManagementService.create(dto));
        }
        if (eventsManagements.size() > 0)
            return new ResponseEntity<>(eventsManagements.stream().map(em -> {return eventsManagementService.toDto(em);})
                    .collect(Collectors.toList()), HttpStatus.CREATED);
        else return ResponseEntity.badRequest().build();
    }

    // DELETE EventsManagement
    @DeleteMapping("eventsmanagement/{emId}")
    public ResponseEntity<?> deleteEventsManagement(@PathVariable Long emId) {
        if (eventsManagementRepository.findById(emId).isPresent()) {

            eventsManagementService.deleteById(emId);
            return ResponseEntity.ok().build();

        } else return ResponseEntity.notFound().build();
    }

    // GET all TriggerSchedules
    @GetMapping("/triggerschedules")
    public ResponseEntity<?> getAllTriggerSchedules() {
        return new ResponseEntity<>(triggerSchedulesRepository.findAll(), HttpStatus.OK);
    }

    // PUT TriggerSchedules
    @PutMapping("/triggerschedules/{tsId}")
    public ResponseEntity<?> putTriggerSchedules(@RequestBody @Valid TriggerSchedules dto,
                                              @PathVariable Long tsId) {
       if (triggerSchedulesRepository.existsById(tsId)) {
            TriggerSchedules ts = triggerSchedulesRepository.findByDeletedFalseAndAndTriggerScheduleId(tsId).get();
            // set the 2 attributes that were ignored in JSON
            dto.setDeleted(ts.getDeleted());
            dto.setEventsManagement(ts.getEventsManagement());

            return new ResponseEntity<>(triggerSchedulesRepository.save(dto), HttpStatus.OK);
       }
       return ResponseEntity.notFound().build();
    }
}
