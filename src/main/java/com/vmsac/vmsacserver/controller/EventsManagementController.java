package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import com.vmsac.vmsacserver.repository.*;
import com.vmsac.vmsacserver.service.*;
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
    EventsManagementNotificationService eventsManagementNotificationService;

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

        // GEN_OUT_1,2,3; NOTIFICATION (SMS); NOTIFICATION (EMAIL)
        final String[] typeNamesForController = {"GEN_OUT_1", "GEN_OUT_2", "GEN_OUT_3", "NOTIFICATION (SMS)", "NOTIFICATION (EMAIL)"};

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
            if (outputTypeRepository.existsById(dto.getEventActionOutputType().getEventActionOutputId()))
                return new ResponseEntity<>(outputEventRepository.save(dto), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // GET EventsManagement
    @GetMapping("eventsmanagement")
    public ResponseEntity<?> getAllEventsManagement() {
        List<EventsManagement> ems = new ArrayList<>();
        System.out.println("checkpoint 1");
        controllerRepository.findByDeletedIsFalseOrderByCreatedDesc().forEach(controller -> {
            ems.addAll(controller.getEventsManagements());
        });
        System.out.println("checkpoint 2");
        entranceRepo.findByDeleted(false).forEach(entrance -> {
            System.out.println(entrance.getEventsManagements());
            ems.addAll(entrance.getEventsManagements());
        });
        System.out.println("checkpoint 3");
        //System.out.println(ems);
        return new ResponseEntity<>(ems.stream()
                .map(em -> eventsManagementService.toDto(em))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    // Get EventsManagementNotifications for a single events management
    @GetMapping("eventsmanagement/notifications/{eventsManagementId}")
    public ResponseEntity<?> getEventsManagementNotifications(@PathVariable Long eventsManagementId) {
        List<EventsManagementNotification> notifs = eventsManagementNotificationService.findByEventsManagementIdNotDeleted(eventsManagementId);

        return new ResponseEntity<>(notifs.stream().collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("eventsmanagement/notifications") // map of emIds to list of EM notifications
    public ResponseEntity<?> getAllEventsManagementNotifications() {
        List<EventsManagementNotification> notifs = eventsManagementNotificationService.findAllNotDeleted();
        Map<Long, List<EventsManagementNotification>> emsToNotifs = new HashMap<>();
        for (int i = 0; i < notifs.size(); i++) {
            EventsManagementNotification notif = notifs.get(i);
            if (emsToNotifs.containsKey(notif.getEventsManagement().getEventsManagementId())) {
                List<EventsManagementNotification> notifList = emsToNotifs.get(notif.getEventsManagement().getEventsManagementId());
                notifList.add(notif);
            } else {
                List<EventsManagementNotification> newNotifList = new ArrayList<>();
                newNotifList.add(notif);
                emsToNotifs.put(notif.getEventsManagement().getEventsManagementId(), newNotifList);
            }
        }
        return new ResponseEntity<>(emsToNotifs, HttpStatus.OK);
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
        if (optionalController.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<EventsManagement> eventManagements = optionalController.get().getEventsManagements();

        // also get the events managements of entrances that are linked to this controller
        List<AuthDevice> authdevicelist = authDeviceService.findbyControllerId(controllerId);

        authdevicelist.forEach(authdevice -> {
            if (authdevice.getEntrance() != null) {
                authdevice.getEntrance().getEventsManagements().forEach(em -> {
                    if (!eventManagements.contains(em)) eventManagements.add(em);
                });
            }
        });
        return new ResponseEntity<>(eventManagements
                .stream().map(em -> eventsManagementService.toDto(em))
                .collect(Collectors.toList()), HttpStatus.OK);
    }


    @PutMapping("eventsmanagement/{emId}")
    public ResponseEntity<?> putEventsMangement(@RequestBody @Valid EventsManagement dto,
                                                @PathVariable Long emId) {
        if (eventsManagementRepository.existsByDeletedFalseAndEventsManagementId(emId)) {
            EventsManagement em = eventsManagementRepository.findByDeletedFalseAndEventsManagementId(emId).get();
            // set the attributes that were ignored in JSON
            // check valid GEN IN/OUT
            // 1. set to current inputs outputs to null
            // 2. same check  checkValidEm
            // 3a. if pass, save new ems
            // 3b. if fail, retain old ems
            dto.setDeleted(em.getDeleted());
            dto.setController(em.getController());
            dto.setEntrance(em.getEntrance());
            return new ResponseEntity<>(eventsManagementService.toDto(eventsManagementRepository.save(dto)), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // check valid EventsManagement with valid GEN IN/OUT
    private void checkValidEm(EventsManagementCreateDto dto, Set<EventsManagement> errorEvMs,
                              Map<String, List<EventsManagementDto>> errorList,
                              List<Map<String, List<EventsManagementDto>>> returnErrorList,
                              List<Long> controllerIds, List<Long> entranceIds,
                              List<Long> thisControllerIds, List<Long> thisEntranceIds) {
        // check input events
        for (InputEvent e : dto.getInputEvents()) {
            String name = inputTypeRepository.findById(e.getEventActionInputType().getEventActionInputId())
                    .get().getEventActionInputName();
            if (name.startsWith("GEN_IN_")) {
                for (Long id : controllerIds) {
                    Controller c = controllerRepository.findByControllerIdEqualsAndDeletedFalse(id).get();
                    Set<EventsManagement> thisEms = c.getAllEventsManagement();
                    thisEms.forEach(em -> {
                        for (OutputEvent oe : outputEventRepository.findAllById(em.getOutputActionsId())) {
                            if (oe.getEventActionOutputType().getEventActionOutputName().equals(
                                    "GEN_OUT_" + name.substring(7))) {
                                errorEvMs.add(em);
                                thisControllerIds.remove(id);

                                if (errorList.containsKey(dto.getEventsManagementName())) {
                                    //if key is used,add to value
                                    errorList.get(dto.getEventsManagementName()).add(eventsManagementService.toDto(em));
                                } else { //adds key,value pair.
                                    List<EventsManagementDto> addToValue = new ArrayList<>();
                                    addToValue.add(eventsManagementService.toDto(em));
                                    errorList.put(dto.getEventsManagementName(), addToValue);
                                }
                                break;
                            }
                        }
                    });
                }
                for (Long id : entranceIds) {
                    Entrance entrance = entranceRepo.findByEntranceIdAndDeletedFalse(id).get();
                    Set<EventsManagement> thisEms;
                    if (entrance.getAssignedController() == null)
                        thisEms = new HashSet<>(entrance.getEventsManagements());
                    else
                        thisEms = entrance.getAssignedController().getAllEventsManagement();
                    thisEms.forEach(em -> {
                        for (OutputEvent oe : outputEventRepository.findAllById(em.getOutputActionsId())) {
                            if (oe.getEventActionOutputType().getEventActionOutputName().equals(
                                    "GEN_OUT_" + name.substring(7))) {
                                errorEvMs.add(em);
                                thisEntranceIds.remove(id);

                                if (errorList.containsKey(dto.getEventsManagementName())) {
                                    //if key is used,add to value
                                    errorList.get(dto.getEventsManagementName()).add(eventsManagementService.toDto(em));
                                } else { //adds key,value pair.
                                    List<EventsManagementDto> addToValue = new ArrayList<>();
                                    addToValue.add(eventsManagementService.toDto(em));
                                    errorList.put(dto.getEventsManagementName(), addToValue);
                                }
                                break;
                            }
                        }
                    });
                }
            }
        }

        // check output events
        for (OutputEvent e : dto.getOutputActions()) {
            String name = outputTypeRepository.findById(e.getEventActionOutputType().getEventActionOutputId())
                    .get().getEventActionOutputName();
            if (name.startsWith("GEN_OUT")) {
                // check controller
                for (Long id : controllerIds) {
                    Controller c = controllerRepository.findByControllerIdEqualsAndDeletedFalse(id).get();
                    Set<EventsManagement> thisEms = c.getAllEventsManagement();
                    thisEms.forEach(em -> {
                        for (InputEvent ie : inputEventRepository.findAllById(em.getInputEventsId())) {
                            if (ie.getEventActionInputType().getEventActionInputName().equals(
                                    "GEN_IN_" + name.substring(8))) {
                                errorEvMs.add(em);
                                thisControllerIds.remove(id);

                                if (errorList.containsKey(dto.getEventsManagementName())) {
                                    //if key is used,add to value
                                    errorList.get(dto.getEventsManagementName()).add(eventsManagementService.toDto(em));
                                } else { //adds key,value pair.
                                    List<EventsManagementDto> addToValue = new ArrayList<>();
                                    addToValue.add(eventsManagementService.toDto(em));
                                    errorList.put(dto.getEventsManagementName(), addToValue);
                                }
                                break;
                            }
                        }
                    });
                }

                // check entrance
                for (Long id : entranceIds) {
                    Entrance entrance = entranceRepo.findByEntranceIdAndDeletedFalse(id).get();
                    Set<EventsManagement> thisEms;
                    if (entrance.getAssignedController() == null)
                        thisEms = new HashSet<>(entrance.getEventsManagements());
                    else
                        thisEms = entrance.getAssignedController().getAllEventsManagement();
                    thisEms.forEach(em -> {
                        for (InputEvent ie : inputEventRepository.findAllById(em.getInputEventsId())) {
                            if (ie.getEventActionInputType().getEventActionInputName().equals(
                                    "GEN_IN_" + name.substring(8))) {
                                errorEvMs.add(em);
                                thisEntranceIds.remove(id);

                                if (errorList.containsKey(dto.getEventsManagementName())) {
                                    //if key is used,add to value
                                    errorList.get(dto.getEventsManagementName()).add(eventsManagementService.toDto(em));
                                } else { //adds key,value pair.
                                    List<EventsManagementDto> addToValue = new ArrayList<>();
                                    addToValue.add(eventsManagementService.toDto(em));
                                    errorList.put(dto.getEventsManagementName(), addToValue);
                                }
                                break;
                            }
                        }
                    });
                }
            }
        }
    }

    // Replace all eventsmanagement with newly created ones
    @PutMapping("eventsmanagement/replace")
    public ResponseEntity<?> replaceEventsMangement(@RequestBody @Valid List<EventsManagementCreateDto> dtos,
                                                    @RequestParam("controllerIds") List<Long> controllerIds,
                                                    @RequestParam("entranceIds") List<Long> entranceIds) throws NotFoundException {

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
        Set<EventsManagement> errorEvMs = new HashSet<>();
        List<Map<String, List<EventsManagementDto>>> returnErrorList = new ArrayList<>();
        Map<String, List<EventsManagementDto>> errorList = new HashMap<>();

        for (EventsManagementCreateDto dto : dtos) {

            List<Long> thisControllerIds = new ArrayList<>(controllerIds);
            List<Long> thisEntranceIds = new ArrayList<>(entranceIds);

            checkValidEm(dto, errorEvMs, errorList, returnErrorList, controllerIds, entranceIds, thisControllerIds, thisEntranceIds);

            eventsManagements.addAll(eventsManagementService.create(dto, thisControllerIds, thisEntranceIds));
        }

        if (!errorList.isEmpty()) {
            returnErrorList.add(errorList);
        }

        if (errorEvMs.isEmpty())
            return new ResponseEntity<>(eventsManagements.stream().map(em -> {
                        return eventsManagementService.toDto(em);
                    })
                    .collect(Collectors.toList()), HttpStatus.CREATED);
        else return new ResponseEntity<>(returnErrorList, HttpStatus.CONFLICT);
    }

    @PutMapping("eventsmanagement/add")
    public ResponseEntity<?> addEventsManagement(@RequestBody @Valid List<EventsManagementCreateDto> dtos,
                                                 @RequestParam("controllerIds") List<Long> controllerIds,
                                                 @RequestParam("entranceIds") List<Long> entranceIds) throws NotFoundException {

        List<EventsManagement> eventsManagements = new ArrayList<>();
        Set<EventsManagement> errorEvMs = new HashSet<>();
        List<Map<String, List<EventsManagementDto>>> returnErrorList = new ArrayList<>();
        Map<String, List<EventsManagementDto>> errorList = new HashMap<>();

        for (EventsManagementCreateDto dto : dtos) {

            List<Long> thisControllerIds = new ArrayList<>(controllerIds);
            List<Long> thisEntranceIds = new ArrayList<>(entranceIds);
            System.out.println("check valid starting");
            checkValidEm(dto, errorEvMs, errorList, returnErrorList, controllerIds, entranceIds, thisControllerIds, thisEntranceIds);
            System.out.println("check add all starting");
            eventsManagements.addAll(eventsManagementService.create(dto, thisControllerIds, thisEntranceIds));
            System.out.println("check add completed");
        }

        if (!errorList.isEmpty()) {
            returnErrorList.add(errorList);
        }

        if (errorEvMs.isEmpty())
            return new ResponseEntity<>(eventsManagements.stream().map(em -> {
                        return eventsManagementService.toDto(em);
                    })
                    .collect(Collectors.toList()), HttpStatus.CREATED);
        else return new ResponseEntity<>(returnErrorList, HttpStatus.CONFLICT);
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
            } else return ResponseEntity.notFound().build();
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

    // GET Individual Event Management
    @GetMapping("eventsmanagement/{emId}")
    public ResponseEntity<?> getIndividualEventsManagement(@PathVariable Long emId) {
        Optional<EventsManagement> eventsManagementFound = eventsManagementRepository.findByDeletedFalseAndEventsManagementId(emId);
        if (eventsManagementFound.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        EventsManagement found_ems = eventsManagementFound.get();
        return new ResponseEntity<>(
                eventsManagementService.toDto(found_ems), HttpStatus.OK);
    }


}


