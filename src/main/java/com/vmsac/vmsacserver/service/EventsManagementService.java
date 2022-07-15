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

    public List<EventsManagement> create(EventsManagementCreateDto dto) {

        List<EventsManagement> resultEms = new ArrayList<>();

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
                    return resultEms;
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
                    return resultEms;
            }
        }

        for (Integer controllerId : dto.getControllerIds()) {
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
        EventEntranceDto entrance = new EventEntranceDto();
        EventControllerDto controller = new EventControllerDto();
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
