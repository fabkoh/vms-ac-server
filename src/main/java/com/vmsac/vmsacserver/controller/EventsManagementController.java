package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.repository.*;
import com.vmsac.vmsacserver.service.AuthDeviceService;
import com.vmsac.vmsacserver.service.ControllerService;
import com.vmsac.vmsacserver.service.InOutEventService;
import com.vmsac.vmsacserver.service.EventsManagementService;
import com.vmsac.vmsacserver.service.TriggerSchedulesService;
import com.vmsac.vmsacserver.util.DateTimeParser;
import com.vmsac.vmsacserver.util.FieldsModifier;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.*;
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
    EntranceRepository entranceRepo;

    @Autowired
    ControllerRepository controllerRepository;

    @Autowired
    InputEventRepository inputEventRepository;

    @Autowired
    EventActionOutputTypeRepository outputTypeRepository;

    @Autowired
    OutputEventRepository outputEventRepository;

    @Autowired
    InOutEventService inOutEventService;

    @Autowired
    FieldsModifier fieldsModifier;

    @Autowired
    DateTimeParser dateTimeParser;

    @Autowired
    GENConfigsRepository genRepo;

    // GET ALL EventActionInputType
    @GetMapping("/event/input/types")
    public ResponseEntity<?> getAllInputTypes(@RequestParam("forController") Boolean forController) {

        // FIRE; GEN_IN_1,2,3
        final String[] typeNamesForController = {"FIRE", "GEN_IN_1", "GEN_IN_2", "GEN_IN_3"};

        if (forController) {
            return new ResponseEntity<>(inputTypeRepository.findAllByEventActionInputNameIgnoreCaseIn(
                    typeNamesForController), HttpStatus.OK);
        } else
            return new ResponseEntity<>(inputTypeRepository.findAll(), HttpStatus.OK);
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

    // GET ALL EventActionOutputType
    @GetMapping("/event/output/types")
    public ResponseEntity<?> getAllOutputTypes(@RequestParam("forController") Boolean forController) {

        // GEN_OUT_1,2,3; NOTIFICATION
        final String[] typeNamesForController = {"GEN_OUT_1", "GEN_OUT_2", "GEN_OUT_3", "NOTIFICATION"};

        if (forController) {
            return new ResponseEntity<>(outputTypeRepository.findAllByEventActionOutputNameIgnoreCaseIn(
                    typeNamesForController), HttpStatus.OK);
        } else
            return new ResponseEntity<>(outputTypeRepository.findAll(), HttpStatus.OK);
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
    public ResponseEntity<?> postEventsMangement(@RequestBody @Valid EventsManagementCreateDto dto) throws NotFoundException {

        List<EventsManagement> eventsManagements = eventsManagementService.create(dto);

        if (eventsManagements.size() > 0)
            return new ResponseEntity<>(eventsManagements.stream().map(em -> {return eventsManagementService.toDto(em);})
                    .collect(Collectors.toList()), HttpStatus.CREATED);
        else return ResponseEntity.badRequest().build();
    }

    // GET EventsManagement
    @GetMapping("eventsmanagement")
    public ResponseEntity<?> getAllEventsMangement() {
        List<EventsManagement> ems = new ArrayList<>();

        controllerRepository.findByDeletedIsFalseOrderByCreatedDesc().forEach(controller -> {
            ems.addAll(controller.getEventsManagements());
        });

        entranceRepo.findByDeleted(false).forEach(entrance -> {
            ems.addAll(entrance.getEventsManagements());
        });

        return new ResponseEntity<>(ems.stream()
                .map(em -> eventsManagementService.toDto(em))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    // GET EventsManagement Entrance
    @GetMapping("eventsmanagement/entrance/{entranceId}")
    public ResponseEntity<?> getAllEventsMangementForEntrance(@PathVariable Long entranceId) {
        if (entranceRepo.existsByEntranceId(entranceId))
            return new ResponseEntity<>(entranceRepo.findByEntranceIdAndDeletedFalse(entranceId)
                .get().getEventsManagements()
                .stream().map(em -> eventsManagementService.toDto(em))
                .collect(Collectors.toList()), HttpStatus.OK);

        else return ResponseEntity.notFound().build();
    }

    // GET EventsManagement Controller
    @GetMapping("eventsmanagement/controller/{controllerId}")
    public ResponseEntity<?> getAllEventsMangementForController(@PathVariable Long controllerId) {
        Optional<Controller> optionalController = controllerService.findById(controllerId);
        if (optionalController.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<EventsManagement> eventManagements = optionalController.get().getEventsManagements();

        // also get the events managements of entrances that are linked to this controller
        List<AuthDevice> authdevicelist = authDeviceService.findbyControllerId(controllerId);

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
                                                    @RequestParam("controllerIds") List<Integer> controllerReqIds,
                                                    @RequestParam("entranceIds") List<Integer> entranceReqIds) throws NotFoundException {
        List<Long> controllerIds = controllerReqIds.stream().map(Integer::longValue).collect(Collectors.toList());
        List<Long> entranceIds = entranceReqIds.stream().map(Integer::longValue).collect(Collectors.toList());

        for (Long controllerId : controllerIds) {
            Optional<Controller> opCon = controllerRepository.findByControllerIdEqualsAndDeletedFalse(controllerId);
            if (opCon.isPresent())
                for (EventsManagement em : opCon.get().getEventsManagements()) {
                    eventsManagementService.deleteById(em.getEventsManagementId());
                }
        }

        for (Long entranceId : entranceIds) {
            Optional<Entrance> opEnt = entranceRepo.findByEntranceIdAndDeletedFalse(entranceId);
            if (opEnt.isPresent())
                for (EventsManagement em : opEnt.get().getEventsManagements()) {
                    eventsManagementService.deleteById(em.getEventsManagementId());
                }
        }

        List<EventsManagement> eventsManagements = new ArrayList<>();

        for (EventsManagementCreateDto dto : dtos) {

            // get ALL related controllers
            entranceIds.forEach(id -> {
                Optional<Entrance> op = entranceRepo.findByEntranceIdAndDeletedFalse(id);
                List<AuthDevice> authDevices = op.get().getEntranceAuthDevices();
                if (!authDevices.isEmpty()) {
                    Long controllerId = authDevices.get(0).getController().getControllerId();
                    if (!controllerIds.contains(controllerId))
                        controllerIds.add(controllerId);
                }
            });

            // check input events
            for (InputEvent e : dto.getInputEvents()) {
                String name = inputTypeRepository.findById(e.getEventActionInputType().getEventActionInputId())
                        .get().getEventActionInputName();
                if (name.startsWith("GEN_IN")) {
                    for (Long id : controllerIds) {
                        GENConfigs gen = genRepo.getByController_ControllerIdAndPinName(id, name.substring(7));
                        if (gen.getStatus() != null && gen.getStatus().equals("OUT")) {
                            Controller c = controllerRepository.findByControllerIdEqualsAndDeletedFalse(id).get();
                            return new ResponseEntity<>("Conflict GEN IN/OUT " + name + " at controller "
                                    + c.getControllerName() + ". Please check again!", HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            }

            // check output events
            for (OutputEvent e : dto.getOutputActions()) {
                String name = outputTypeRepository.findById(e.getEventActionOutputType().getEventActionOutputId())
                        .get().getEventActionOutputName();
                if (name.startsWith("GEN_OUT")) {
                    for (Long id : controllerIds) {
                        GENConfigs gen = genRepo.getByController_ControllerIdAndPinName(id, name.substring(8));
                        if (gen.getStatus() != null && gen.getStatus().equals("IN")) {
                            Controller c = controllerRepository.findByControllerIdEqualsAndDeletedFalse(id).get();
                            return new ResponseEntity<>("Conflict GEN IN/OUT " + name + " at controller "
                                    + c.getControllerName() + ". Please check again!", HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            }

            dto.setControllerIds(controllerReqIds);
            dto.setEntranceIds(entranceReqIds);
            eventsManagements.addAll(eventsManagementService.create(dto));
        }
        if (eventsManagements.size() > 0)
            return new ResponseEntity<>(eventsManagements.stream().map(em -> {return eventsManagementService.toDto(em);})
                    .collect(Collectors.toList()), HttpStatus.CREATED);
        else return ResponseEntity.badRequest().build();
    }

    @PutMapping("eventsmanagement/add")
    public ResponseEntity<?> addEventsManagement(@RequestBody @Valid List<EventsManagementCreateDto> dtos,
                                                    @RequestParam("controllerIds") List<Integer> controllerReqIds,
                                                    @RequestParam("entranceIds") List<Integer> entranceReqIds) throws NotFoundException {

        List<EventsManagement> eventsManagements = new ArrayList<>();

        for (EventsManagementCreateDto dto : dtos) {

            // get ALL related controllers
            List<Long> controllerIds = controllerReqIds.stream().map(Integer::longValue).collect(Collectors.toList());
            entranceReqIds.forEach(id -> {
                Optional<Entrance> op = entranceRepo.findByEntranceIdAndDeletedFalse(id.longValue());
                List<AuthDevice> authDevices = op.get().getEntranceAuthDevices();
                if (!authDevices.isEmpty()) {
                    Long controllerId = authDevices.get(0).getController().getControllerId();
                    if (!controllerIds.contains(controllerId))
                        controllerIds.add(controllerId);
                }
            });

            // check input events
            for (InputEvent e : dto.getInputEvents()) {
                String name = inputTypeRepository.findById(e.getEventActionInputType().getEventActionInputId())
                        .get().getEventActionInputName();
                if (name.startsWith("GEN_IN")) {
                    for (Long id : controllerIds) {
                        GENConfigs gen = genRepo.getByController_ControllerIdAndPinName(id, name.substring(7));
                        if (gen.getStatus() != null && gen.getStatus().equals("OUT")) {
                            Controller c = controllerRepository.findByControllerIdEqualsAndDeletedFalse(id).get();
                            return new ResponseEntity<>("Conflict GEN IN/OUT " + name + " at controller "
                                    + c.getControllerName() + ". Please check again!", HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            }

            // check output events
            for (OutputEvent e : dto.getOutputActions()) {
                String name = outputTypeRepository.findById(e.getEventActionOutputType().getEventActionOutputId())
                        .get().getEventActionOutputName();
                if (name.startsWith("GEN_OUT")) {
                    for (Long id : controllerIds) {
                        GENConfigs gen = genRepo.getByController_ControllerIdAndPinName(id, name.substring(8));
                        if (gen.getStatus() != null && gen.getStatus().equals("IN")) {
                            Controller c = controllerRepository.findByControllerIdEqualsAndDeletedFalse(id).get();
                            return new ResponseEntity<>("Conflict GEN IN/OUT " + name + " at controller "
                                    + c.getControllerName() + ". Please check again!", HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            }
            dto.setControllerIds(controllerReqIds);
            dto.setEntranceIds(entranceReqIds);
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

    // DELETE All events-management of a list of controllers
    @DeleteMapping("eventsmanagement/controller")
    public ResponseEntity<?> deleteForController(@RequestParam("controllerIds") List<Long> controllerIds) {
        for (Long id : controllerIds) {
            Optional<Controller> opController = controllerRepository.findByControllerIdEqualsAndDeletedFalse(id);
            if (opController.isPresent()) {
                opController.get().getEventsManagements().forEach(em -> {
                    eventsManagementService.deleteById(em.getEventsManagementId());
                });
            }
            else return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    // DELETE All events-management of a list of entrances
    @DeleteMapping("eventsmanagement/entrance")
    public ResponseEntity<?> deleteForEntrance(@RequestParam("entranceIds") List<Long> entranceIds) {
        for (Long id : entranceIds) {
            Optional<Entrance> opEntrance = entranceRepo.findByEntranceIdAndDeletedFalse(id);
            if (opEntrance.isPresent()) {
                opEntrance.get().getEventsManagements().forEach(em -> {
                    eventsManagementService.deleteById(em.getEventsManagementId());
                });
            } else return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
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
