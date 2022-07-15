package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.EventDto.EventControllerDto;
import com.vmsac.vmsacserver.model.EventDto.EventEntranceDto;
import com.vmsac.vmsacserver.repository.*;
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
    EventService eventService;

    public List<Long> createInputEvents(List<InputEvent> dto) {
        List<Long> inputEventsId = new ArrayList<>();
        for (InputEvent input : dto) {
            Long id = input.getInputEventId();
            if (id == null) {
                inputEventsId.add(eventService.createInputEvent(input).getInputEventId());
            } else {
                inputEventsId.add(id);
            }
        }
        return inputEventsId;
    }

    public List<Long> createOutputActions(List<OutputEvent> dto) {
        List<Long> outputActionsId = new ArrayList<>();
        for (OutputEvent output : dto) {
            Long id = output.getOutputEventId();
            if (id == null) {
                outputActionsId.add(eventService.createOutputEvent(output).getOutputEventId());
            } else {
            outputActionsId.add(id);
            }
        }
        return outputActionsId;
    }

    public List<EventsManagement> create(EventsManagementCreateDto dto) {

        List<EventsManagement> resultEms = new ArrayList<>();

        for (Integer controllerId : dto.getControllerIds()) {
            // create different input and output events for each eventsManagement
            // in case users want to modify input/output events
            List<Long> inputEventsId = createInputEvents(dto.getInputEvents());
            List<Long> outputActionsId = createOutputActions(dto.getOutputEvents());

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
            List<Long> inputEventsId = createInputEvents(dto.getInputEvents());
            List<Long> outputActionsId = createOutputActions(dto.getOutputEvents());

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
