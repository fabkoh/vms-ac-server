package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.repository.*;
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
    EventService eventService;

    public Optional<EventsManagement> create(EventsManagementCreateDto dto) {

        // check if this controller exists
        Optional<Controller> opController = controllerRepository.findByControllerIdEqualsAndDeletedFalse(dto.getControllerId());

        // check if this entrance exists
        Optional<Entrance> opEntrance = entranceRepository.findByEntranceIdAndDeletedFalse(dto.getEntranceId());

        // create input events
        List<Long> inputEventsId = new ArrayList<>();
        for (InputEvent input : dto.getInputEvents()) {
            Long id = input.getInputEventId();
            if (id == null) {
                Optional<InputEvent> opInput = eventService.createInputEvent(input);
                if (opInput.isPresent()) {
                    inputEventsId.add(opInput.get().getInputEventId());
                }
            } else {
                if (inputEventRepository.existsById(id))
                    inputEventsId.add(id);
                else
                    return Optional.empty();
            }
        }

        // create output events
        List<Long> outputActionsId = new ArrayList<>();
        for (OutputEvent output : dto.getOutputEvents()) {
            Long id = output.getOutputEventId();
            if (id == null) {
                Optional<OutputEvent> opOutput = eventService.createOutputEvent(output);
                if (opOutput.isPresent()) {
                    outputActionsId.add(opOutput.get().getOutputEventId());
                }
            } else {
                if (outputEventRepository.existsById(id))
                    outputActionsId.add(id);
                else
                    return Optional.empty();
            }
        }

        if (opController.isPresent()) {
            EventsManagement em = eventsManagementRepository.save(new EventsManagement(null,
                            dto.getEventsManagementName(), false, inputEventsId,
                            outputActionsId, opController.get(), null, dto.getTriggerSchedules()));

            for (TriggerSchedules ts : em.getTriggerSchedules()) {
                ts.setEventsManagement(em);
                triggerSchedulesRepository.save(ts);
            }

            return Optional.of(em);
        } else if (opEntrance.isPresent()) {
            EventsManagement em = eventsManagementRepository.save(new EventsManagement(null,
                            dto.getEventsManagementName(), false, inputEventsId,
                            outputActionsId, null, opEntrance.get(), dto.getTriggerSchedules()));

            for (TriggerSchedules ts : em.getTriggerSchedules()) {
                ts.setEventsManagement(em);
                triggerSchedulesRepository.save(ts);
            }

            return Optional.of(em);
        }
        else return Optional.empty();
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
}
