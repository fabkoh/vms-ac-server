package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.repository.*;
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
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@Validated
@RequestMapping("/api")
public class EventsManagementController {

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
        Optional<EventActionInputType> opEait = inputTypeRepository.findById(
                dto.getEventActionInputType().getEventActionInputId());

        if (opEait.isPresent()) {
            EventActionInputType type = opEait.get();
            if ((type.getTimerEnabled() && dto.getTimerDuration() == null)
            || (!type.getTimerEnabled() && dto.getTimerDuration() != null)) {
                    return new ResponseEntity<>(type, HttpStatus.BAD_REQUEST);
            }
            InputEvent ie = inputEventRepository.save(new InputEvent(null,
                    dto.getTimerDuration(), type));
            return new ResponseEntity<>(ie, HttpStatus.CREATED);
        }
        else return ResponseEntity.notFound().build();

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

        Optional<EventActionOutputType> opType = outputTypeRepository.findById(
                dto.getEventActionOutputType().getEventActionOutputId());

        if(opType.isPresent()) {

            EventActionOutputType type = opType.get();
            if ((type.getTimerEnabled() && dto.getTimerDuration() == null)
                    || (!type.getTimerEnabled() && dto.getTimerDuration() != null)) {
                return new ResponseEntity<>(type, HttpStatus.BAD_REQUEST);
            }

            OutputEvent oe = outputEventRepository.save(new OutputEvent(null,
                    dto.getTimerDuration(), type));
            return new ResponseEntity<>(oe, HttpStatus.CREATED);
        }
        return ResponseEntity.notFound().build();
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

    // POST EventsManagement for controller
    @PostMapping("eventsmanagement/controller")
    public ResponseEntity<?> postForController(
            @RequestBody @Valid EventsManagementCreateDto dto) {

        Optional<EventsManagement> toCreate = eventsManagementService.createForController(dto);

        if (toCreate.isPresent())
            return new ResponseEntity<>(toCreate.get(), HttpStatus.CREATED);
        else return ResponseEntity.notFound().build();
    }

    // POST EventsManagement for Entrance
    @PostMapping("eventsmanagement/entrance")
    public ResponseEntity<?> postForEntrance(@RequestBody @Valid EventsManagementCreateDto dto) {

        Optional<EventsManagement> opEm = eventsManagementService.createForEntrance(dto);

        if (opEm.isPresent())
            return new ResponseEntity<>(opEm.get(), HttpStatus.CREATED);
        else return ResponseEntity.badRequest().build();
    }

    // GET EventsManagement
    @GetMapping("eventsmanagements")
    public ResponseEntity<?> getAllEventsMangement() {
        return new ResponseEntity<>(eventsManagementRepository.findAllByDeletedFalse(),
                HttpStatus.OK);
    }

//    // PUT EventsManagement's Controller
//    @PutMapping("eventsmanagement/{emId}/controller")
//    public ResponseEntity<?> putController(@RequestBody Map<String, Long> controllerId,
//                                           @PathVariable Long emId) {
//
//        Optional<EventsManagement> opEm = eventsManagementRepository.findByDeletedFalseAndEventsManagementId(emId);
//
//        Optional<Controller> opCon = controllerRepository.findByControllerIdEqualsAndDeletedFalse(controllerId.get("controllerId"));
//
//        if (opEm.isPresent() && opCon.isPresent()) {
//            EventsManagement em = opEm.get();
//            Controller con = opCon.get();
//            em.setController(con);
//            em = eventsManagementRepository.save(em);
//            return new ResponseEntity<>(em, HttpStatus.OK);
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    // PUT EventsManagement's Entrance
//    @PutMapping("eventsmanagement/{emId}/entrance")
//    public ResponseEntity<?> putEntrance(@RequestBody Map<String, Long> entranceId,
//                                         @PathVariable Long emId) {
//
//        Optional<EventsManagement> opEm = eventsManagementRepository.findByDeletedFalseAndEventsManagementId(emId);
//
//        Optional<Entrance> opEnt = entranceRepository.findByEntranceIdAndDeletedFalse(entranceId.get("entranceId"));
//
//        if (opEm.isPresent() && opEnt.isPresent()) {
//            EventsManagement em = opEm.get();
//            Entrance ent = opEnt.get();
//            em.setEntrance(ent);
//            em = eventsManagementRepository.save(em);
//            return new ResponseEntity<>(em, HttpStatus.OK);
//        }
//        return ResponseEntity.notFound().build();
//    }

    // PUT EventsMangement
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
