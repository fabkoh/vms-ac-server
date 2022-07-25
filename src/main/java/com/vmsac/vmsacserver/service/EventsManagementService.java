package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.EventDto.EventControllerDto;
import com.vmsac.vmsacserver.model.EventDto.EventEntranceDto;
import com.vmsac.vmsacserver.repository.*;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    GENConfigsRepository genRepo;

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

    public List<EventsManagement> create(EventsManagementCreateDto dto) throws NotFoundException {

        List<EventsManagement> resultEms = new ArrayList<>();
        List<Long> controllerIds = dto.getControllerIds().stream().map(Integer::longValue).collect(Collectors.toList());

        for (Long controllerId : controllerIds) {
            // create different input and output events for each eventsManagement
            // in case users want to modify input/output events
            List<Long> inputEventsId = createInputEvents(dto.getInputEvents(), controllerId);
            List<Long> outputActionsId = createOutputActions(dto.getOutputActions(), controllerId);

            Optional<Controller> opController = controllerRepository.findByControllerIdEqualsAndDeletedFalse(controllerId.longValue());
            if (opController.isPresent()) {
                EventsManagement em = eventsManagementRepository.save(new EventsManagement(null,
                        dto.getEventsManagementName(), false, inputEventsId,
                        outputActionsId, opController.get(), null, new ArrayList<>()));

                for (TriggerSchedules ts : dto.getTriggerSchedules()) {
                    TriggerSchedules newTs = triggerSchedulesRepository.save(new TriggerSchedules(
                            null, ts.getTriggerName(), ts.getRrule(), ts.getTimeStart(),
                            ts.getTimeEnd(), false, em));

                    em.getTriggerSchedules().add(newTs);
                }
                resultEms.add(em);
            }
        }

        for (Integer entranceId : dto.getEntranceIds()) {
            // create different input and output events for each eventsManagement
            // in case users want to modify input/output events
            List<AuthDevice> devices = entranceRepository.findByEntranceIdAndDeletedFalse(entranceId.longValue())
                    .get().getEntranceAuthDevices();
            Long controllerId = null;
            if (!devices.isEmpty()) {
                controllerId = devices.get(0).getController().getControllerId();
            }
            List<Long> inputEventsId = createInputEvents(dto.getInputEvents(), controllerId);
            List<Long> outputActionsId = createOutputActions(dto.getOutputActions(), controllerId);

            Optional<Entrance> opEntrance = entranceRepository.findByEntranceIdAndDeletedFalse(entranceId.longValue());
            if (opEntrance.isPresent()) {
                EventsManagement em = eventsManagementRepository.save(new EventsManagement(null,
                        dto.getEventsManagementName(), false, inputEventsId,
                        outputActionsId, null, opEntrance.get(), new ArrayList<>()));

                for (TriggerSchedules ts : dto.getTriggerSchedules()) {
                    TriggerSchedules newTs = triggerSchedulesRepository.save(new TriggerSchedules(
                            null, ts.getTriggerName(), ts.getRrule(), ts.getTimeStart(),
                            ts.getTimeEnd(), false, em));

                    em.getTriggerSchedules().add(newTs);
                }
                resultEms.add(em);
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
                controller);
    }
}
