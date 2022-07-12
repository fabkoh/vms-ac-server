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

    public Optional<EventsManagement> createForController(EventsManagementCreateDto dto) {

        // check if this controller exists
        Optional<Controller> opController = controllerRepository.findById(dto.getControllerId());

        // check if all input events exists
        for (Integer inputId : dto.getInputEventsId()) {
            if (!inputEventRepository.existsById(inputId.longValue()))
                return Optional.empty();
        }
        // check if all output events exists
        for (Integer outputId : dto.getOutputActionsId()) {
            if (!outputEventRepository.existsById(outputId.longValue()))
                return Optional.empty();
        }

        if (opController.isPresent()) {
            return Optional.of(
                    eventsManagementRepository.save(new EventsManagement(null,
                            dto.getEventsManagementName(), false, dto.getInputEventsId(),
                            dto.getOutputActionsId(), opController.get(), null, dto.getTriggerSchedules()))
            );
        } else return Optional.empty();
    }

    public Optional<EventsManagement> createForEntrance(EventsManagementCreateDto dto) {
        // check if this controller exists
        Optional<Entrance> opEntrance = entranceRepository.findByEntranceIdAndDeletedFalse(dto.getEntranceId());

        // check if all input events exists
        for (Integer inputId : dto.getInputEventsId()) {
            if (!inputEventRepository.existsById(inputId.longValue()))
                return Optional.empty();
        }
        // check if all output events exists
        for (Integer outputId : dto.getOutputActionsId()) {
            if (!outputEventRepository.existsById(outputId.longValue()))
                return Optional.empty();
        }

        if (opEntrance.isPresent()) {
            return Optional.of(
                    eventsManagementRepository.save(new EventsManagement(null,
                            dto.getEventsManagementName(), false, dto.getInputEventsId(),
                            dto.getOutputActionsId(), null, opEntrance.get(), dto.getTriggerSchedules()))
            );
        } else return Optional.empty();
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
