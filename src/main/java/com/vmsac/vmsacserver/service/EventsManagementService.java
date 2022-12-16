package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.EventDto.EventControllerDto;
import com.vmsac.vmsacserver.model.EventDto.EventEntranceDto;
import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import com.vmsac.vmsacserver.repository.*;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventsManagementService {

    @Autowired
    EventsManagementRepository eventsManagementRepository;

    @Autowired
    TriggerSchedulesRepository triggerSchedulesRepository;

    @Autowired
    ControllerRepository controllerRepository;

    @Autowired
    EntranceRepository entranceRepository;

    @Autowired
    InputEventRepository inputEventRepository;

    @Autowired
    OutputEventRepository outputEventRepository;

    @Autowired
    InOutEventService inOutEventService;

    @Autowired
    EventsManagementNotificationService eventsManagementNotificationService;

    @Autowired
    GENConfigsRepository genRepo;

    @Autowired
    EventsManagementNotificationRepository eventsManagementNotificationRepository;

    public Optional<EventsManagement> getEventsManagementById(Long eventsManagementId) {
        return eventsManagementRepository.findByDeletedFalseAndEventsManagementId(eventsManagementId);
    }

    public List<Long> createInputEvents(List<InputEvent> dto, Long controllerId) throws NotFoundException {
        List<Long> inputEventsId = new ArrayList<>();
        for (InputEvent input : dto) {
            Long id = input.getInputEventId();
            if (id == null) {
                inputEventsId.add(inOutEventService.createInputEvent(input, controllerId).getInputEventId());
            } else {
                inputEventsId.add(id);
            }
        }
        return inputEventsId;
    }

    public List<Long> createOutputActions(List<OutputEvent> dto, Long controllerId) throws NotFoundException {
        List<Long> outputActionsId = new ArrayList<>();
        for (OutputEvent output : dto) {
            Long id = output.getOutputEventId();
            if (id == null) {
                outputActionsId.add(inOutEventService.createOutputEvent(output, controllerId).getOutputEventId());
            } else {
            outputActionsId.add(id);
            }
        }
        return outputActionsId;
    }

    public List<EventsManagement> create(EventsManagementCreateDto dto,
                                         List<Long> controllerIds,
                                         List<Long> entranceIds) throws NotFoundException {

        List<EventsManagement> resultEms = new ArrayList<>();

        for (Long controllerId : controllerIds) {
            // create different input and output events for each eventsManagement
            // in case users want to modify input/output events
            List<Long> inputEventsId = createInputEvents(dto.getInputEvents(), controllerId);
            List<Long> outputActionsId = createOutputActions(dto.getOutputActions(), controllerId);

            Optional<Controller> opController = controllerRepository.findByControllerIdEqualsAndDeletedFalse(controllerId.longValue());
            if (opController.isPresent()) {
                Controller c = opController.get();
                EventsManagement em = eventsManagementRepository.save(new EventsManagement(null,
                        dto.getEventsManagementName(), false, inputEventsId,
                        outputActionsId, c, null, new ArrayList<>(), new ArrayList<>()));

                for (TriggerSchedules ts : dto.getTriggerSchedules()) {
                    TriggerSchedules newTs = triggerSchedulesRepository.save(new TriggerSchedules(
                            null, ts.getTriggerName(), ts.getRrule(), ts.getTimeStart(),
                            ts.getTimeEnd(), false, em,ts.getDtstart(),ts.getUntil(),ts.getCount(),
                            ts.getRepeatToggle(), ts.getRruleinterval(),ts.getByweekday(),ts.getBymonthday(),
                            ts.getBysetpos(),ts.getBymonth(),ts.getAllDay(),ts.getEndOfDay()));

                    em.getTriggerSchedules().add(newTs);
                }
                if (dto.getEventsManagementEmail() != null) {
                    EventsManagementEmailCreateDto eventManagementEmail = dto.getEventsManagementEmail();
                    EventsManagementNotification newEventManagementNotificationEmail = eventManagementEmail.toEventManagementNotification(false, em);
                    eventsManagementNotificationService.save(newEventManagementNotificationEmail);
                }

                if (dto.getEventsManagementSMS() != null) {
                    EventsManagementSMSCreateDto eventManagementSMS = dto.getEventsManagementSMS();
                    System.out.println(eventManagementSMS);
                    EventsManagementNotification newEventManagementNotificationSMS = eventManagementSMS.toEventManagementNotification(false, em);
                    eventsManagementNotificationService.save(newEventManagementNotificationSMS);
                }
                resultEms.add(em);
                c.getEventsManagements().add(em);
            }
        }

        for (Long entranceId : entranceIds) {
            // create different input and output events for each eventsManagement
            // in case users want to modify input/output events
            List<AuthDevice> devices = entranceRepository.findByEntranceIdAndDeletedFalse(entranceId)
                    .get().getEntranceAuthDevices();
            Long controllerId = null;
            if (!devices.isEmpty()) {
                controllerId = devices.get(0).getController().getControllerId();
            }
            List<Long> inputEventsId = createInputEvents(dto.getInputEvents(), controllerId);
            List<Long> outputActionsId = createOutputActions(dto.getOutputActions(), controllerId);

            Optional<Entrance> opEntrance = entranceRepository.findByEntranceIdAndDeletedFalse(entranceId);
            if (opEntrance.isPresent()) {
                Entrance e = opEntrance.get();
                EventsManagement em = eventsManagementRepository.save(new EventsManagement(null,
                        dto.getEventsManagementName(), false, inputEventsId,
                        outputActionsId, null, e, new ArrayList<>(), new ArrayList<>()));

                for (TriggerSchedules ts : dto.getTriggerSchedules()) {
                    TriggerSchedules newTs = triggerSchedulesRepository.save(new TriggerSchedules(
                            null, ts.getTriggerName(), ts.getRrule(), ts.getTimeStart(),
                            ts.getTimeEnd(), false, em,ts.getDtstart(),ts.getUntil(),ts.getCount(),
                            ts.getRepeatToggle(), ts.getRruleinterval(),ts.getByweekday(),ts.getBymonthday(),
                            ts.getBysetpos(),ts.getBymonth(),ts.getAllDay(),ts.getEndOfDay()));

                    em.getTriggerSchedules().add(newTs);
                }

                if (dto.getEventsManagementEmail() != null) {
                    EventsManagementEmailCreateDto eventManagementEmail = dto.getEventsManagementEmail();
                    EventsManagementNotification newEventManagementNotificationEmail = eventManagementEmail.toEventManagementNotification(false, em);
                    eventsManagementNotificationService.save(newEventManagementNotificationEmail);
                }
                if (dto.getEventsManagementSMS() != null) {
                    EventsManagementSMSCreateDto eventManagementSMS = dto.getEventsManagementSMS();
                    EventsManagementNotification newEventManagementNotificationSMS = eventManagementSMS.toEventManagementNotification(false, em);
                    eventsManagementNotificationService.save(newEventManagementNotificationSMS);
                }
                resultEms.add(em);
                e.getEventsManagements().add(em);
            }
        }

        return resultEms;
    }

    public void deleteById(Long id) {
        Optional<EventsManagement> opEm = eventsManagementRepository.findByDeletedFalseAndEventsManagementId(id);
        if (opEm.isPresent()) {
            EventsManagement em = opEm.get();
//
//            List<InputEvent> inputs = inputEventRepository.findAllById(em.getInputEventsId());
//            List<OutputEvent> outputs = outputEventRepository.findAllById(em.getOutputActionsId());
//
//            inputs.forEach(e -> {
//                String name = e.getEventActionInputType().getEventActionInputName();
//                if (name.startsWith("GEN_IN")) {
//                    Controller c = em.getController();
//                    Entrance ent = em.getEntrance();
//                    if (c == null) {
//                        List<AuthDevice> devices = ent.getEntranceAuthDevices();
//                        if (!devices.isEmpty()) {
//                            c = devices.get(0).getController();
//                        }
//                    }
//                    if (c != null) {
//                        GENConfigs g = genRepo.getByController_ControllerIdAndPinName(c.getControllerId(), name.substring(7));
//                        g.setStatus(null);
//                        genRepo.save(g);
//                    }
//                }
//            });
//
//            outputs.forEach(e -> {
//                String name = e.getEventActionOutputType().getEventActionOutputName();
//                if (name.startsWith("GEN_OUT")) {
//                    Controller c = em.getController();
//                    Entrance ent = em.getEntrance();
//                    if (c == null) {
//                        List<AuthDevice> devices = ent.getEntranceAuthDevices();
//                        if (!devices.isEmpty()) {
//                            c = devices.get(0).getController();
//                        }
//                    }
//                    if (c != null) {
//                        GENConfigs g = genRepo.getByController_ControllerIdAndPinName(c.getControllerId(), name.substring(8));
//                        g.setStatus(null);
//                        genRepo.save(g);
//                    }
//                }
//            });

            // soft delete
            em.setDeleted(true);

            // soft delete all the triggerSchedules
            for (TriggerSchedules ts : em.getTriggerSchedules()) {
                ts.setDeleted(true);
                triggerSchedulesRepository.save(ts);
            }

            eventsManagementRepository.save(em);
        }
    }

    public EventsManagementDto toDto(EventsManagement em) {
        EventEntranceDto entrance;
        EventControllerDto controller;

        if (em.getEntrance() != null)
            entrance = em.getEntrance().toEventDto();
        else entrance = null;

        if (em.getController() != null)
            controller = em.getController().toEventDto();
        else controller = null;

        return new EventsManagementDto(em.getEventsManagementId(), em.getEventsManagementName(),
                inputEventRepository.findAllById(em.getInputEventsId()),
                outputEventRepository.findAllById(em.getOutputActionsId()),
                em.getTriggerSchedules(),
                entrance,
                controller,
                em.getEventsManagementNotifications());
    }
}
