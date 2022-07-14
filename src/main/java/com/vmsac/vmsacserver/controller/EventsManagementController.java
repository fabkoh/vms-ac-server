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

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    // POST InputEvent
    @PostMapping("/event/input")
    public ResponseEntity<?> postInputEvent(@RequestBody @Valid InputEvent dto) {
        Optional<InputEvent> opEvent = eventService.createInputEvent(dto);

        if (opEvent.isPresent()) {
            return new ResponseEntity<>(opEvent.get(), HttpStatus.CREATED);
        }

        else return ResponseEntity.badRequest().build();
    }

    // PUT InputEvent
    @PutMapping("/event/input/{id}")
    public ResponseEntity<?> putInputEvent(@RequestBody @Valid InputEvent dto,
                                           @PathVariable Long id) {

        if (inputEventRepository.existsById(id)) {
            Optional<EventActionInputType> opType = inputTypeRepository
                    .findById(dto.getEventActionInputType().getEventActionInputId());

            if (opType.isPresent()) {
                EventActionInputType type = opType.get();
                if ((type.getTimerEnabled() && dto.getTimerDuration() == null)
                        || (!type.getTimerEnabled() && dto.getTimerDuration() != null)) {
                    return new ResponseEntity<>(type, HttpStatus.BAD_REQUEST);
                }

                return new ResponseEntity<>(inputEventRepository.save(dto), HttpStatus.OK);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE InputEvent
    @DeleteMapping("event/input/{id}")
    public ResponseEntity<?> deleteInputEvent(@PathVariable Long id) {

        if (inputEventRepository.existsById(id)) {
            inputEventRepository.deleteById(id);
            return ResponseEntity.ok().build();
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

    // GET ALL EventActionInputType
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

    // POST OutputEvent
    @PostMapping("event/output")
    public ResponseEntity<?> postOutputEvent(@RequestBody @Valid OutputEvent dto) {

        Optional<OutputEvent> opEvent = eventService.createOutputEvent(dto);

        if (opEvent.isPresent()) {
            return new ResponseEntity<>(opEvent.get(), HttpStatus.CREATED);
        }

        else return ResponseEntity.badRequest().build();
    }

    // PUT OutputEvent
    @PutMapping("event/output/{id}")
    public ResponseEntity<?> putOutputEvent(@RequestBody @Valid OutputEvent dto,
                                            @PathVariable Long id) {
        Optional<OutputEvent> opEvent = outputEventRepository.findById(id);

        if (opEvent.isPresent()) {
            Optional<EventActionOutputType> opType = outputTypeRepository.findById(
                    dto.getEventActionOutputType().getEventActionOutputId());

            if (opType.isPresent()) {
                EventActionOutputType type = opType.get();
                if ((type.getTimerEnabled() && dto.getTimerDuration() == null)
                        || (!type.getTimerEnabled() && dto.getTimerDuration() != null)) {
                    return new ResponseEntity<>(type, HttpStatus.BAD_REQUEST);
                }

                return new ResponseEntity<>(outputEventRepository.save(dto), HttpStatus.OK);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE OutputEvent
    @DeleteMapping("event/output/{id}")
    public ResponseEntity<?> deleteOutputEvent(@PathVariable Long id) {
        if (outputEventRepository.existsById(id)) {
            outputEventRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // POST EventsManagement
    @PostMapping("eventsmanagement")
    public ResponseEntity<?> postEventsMangement(@RequestBody @Valid EventsManagementCreateDto dto) {

        Optional<EventsManagement> opEm = eventsManagementService.create(dto);

        if (opEm.isPresent())
            return new ResponseEntity<>(opEm.get(), HttpStatus.CREATED);
        else return ResponseEntity.badRequest().build();
    }

    // GET EventsManagement
    @GetMapping("eventsmanagements")
    public ResponseEntity<?> getAllEventsMangement() {
        return new ResponseEntity<>(eventsManagementRepository.findAllByDeletedFalse(), HttpStatus.OK);
    }

    // GET EventsManagement Entrance
    @GetMapping("eventsmanagements/entrance/{entranceId}")
    public ResponseEntity<?> getAllEventsMangementForEntrance(@PathVariable Long entranceId) {
        return new ResponseEntity<>(eventsManagementRepository.findByDeletedFalseAndEntrance_EntranceIdOrderByEventsManagementNameAsc(entranceId), HttpStatus.OK);
    }

    // GET EventsManagement Controller
    @GetMapping("eventsmanagements/controller/{controllerId}")
    public ResponseEntity<?> getAllEventsMangementForController(@PathVariable Long controllerId) {
        Optional<Controller> optionalController = controllerService.findById(controllerId);
        if (optionalController.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<AuthDevice> authdevicelist = authDeviceService.findbyControllerId(controllerId);
        List<EventsManagement> eventManagements = eventsManagementRepository.findByDeletedFalseAndController_ControllerIdOrderByEventsManagementNameAsc(controllerId);

        authdevicelist.forEach(authdevice-> {
            try {
                if (authdevice.getEntrance() != null){
                    Entrance entrance = entranceRepository.findByEntranceIdAndDeletedFalse(authdevice.getEntrance().getEntranceId()).get();
                    if (entrance != null) {
                        eventManagements.addAll(eventsManagementRepository.findByDeletedFalseAndEntrance_EntranceIdOrderByEventsManagementNameAsc(entrance.getEntranceId()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return new ResponseEntity<>(eventManagements, HttpStatus.OK);
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
            return new ResponseEntity<>(eventsManagementRepository.save(dto), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE EventsManagement
    @DeleteMapping("eventsmanagement/{emId}")
    public ResponseEntity<?> deleteEventsManagement(@PathVariable Long emId) {
        if (eventsManagementRepository.findById(emId).isPresent()) {

            eventsManagementRepository.deleteById(emId);
            return ResponseEntity.ok().build();

        } else return ResponseEntity.notFound().build();
    }

    // POST TriggerSchedules
    @PostMapping("/triggerschedules")
    public ResponseEntity<?> postTriggerSchedules(@RequestBody @Valid
                                                      TriggerSchedulesCreateDto dto) {
        Optional<TriggerSchedules> opTs = triggerSchedulesService.create(dto);

        if (opTs.isPresent())
            return new ResponseEntity<>(opTs.get(), HttpStatus.CREATED);
        else return ResponseEntity.notFound().build();
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

    // DELETE TriggerSchedules
    @DeleteMapping("/triggerschedules/{tsId}")
    public ResponseEntity<?> deleteTriggerSchedules(@PathVariable Long tsId) {
        Optional<TriggerSchedules> opTs = triggerSchedulesRepository.findByDeletedFalseAndAndTriggerScheduleId(tsId);

        if (opTs.isPresent()) {
            triggerSchedulesRepository.deleteById(tsId);
            return ResponseEntity.ok().build();
        }
        return  ResponseEntity.notFound().build();
    }
}
