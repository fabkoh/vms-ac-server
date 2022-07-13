package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.EventActionInputType;
import com.vmsac.vmsacserver.model.EventActionOutputType;
import com.vmsac.vmsacserver.model.InputEvent;
import com.vmsac.vmsacserver.model.OutputEvent;
import com.vmsac.vmsacserver.repository.EventActionInputTypeRepository;
import com.vmsac.vmsacserver.repository.EventActionOutputTypeRepository;
import com.vmsac.vmsacserver.repository.InputEventRepository;
import com.vmsac.vmsacserver.repository.OutputEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventService {

    @Autowired
    EventActionInputTypeRepository inputTypeRepository;

    @Autowired
    EventActionOutputTypeRepository outputTypeRepository;

    @Autowired
    InputEventRepository inputEventRepository;

    @Autowired
    OutputEventRepository outputEventRepository;

    public boolean checkValidEvent(Integer timerDuration, Boolean timerEnabled) {
        return ((timerEnabled.equals(true) && timerDuration > 0) ||
                (timerEnabled.equals(false) && timerDuration == null));
    }

    public Optional<InputEvent> createInputEvent(InputEvent dto) {
        Optional<EventActionInputType> opType = inputTypeRepository.findById(
                dto.getEventActionInputType().getEventActionInputId());

        if (opType.isPresent()) {
            EventActionInputType type = opType.get();
            if (checkValidEvent(dto.getTimerDuration(), type.getTimerEnabled()))
                return Optional.of(inputEventRepository.save(
                        new InputEvent(null, dto.getTimerDuration(), type)));
        }
        return Optional.empty();
    }

    public Optional<OutputEvent> createOutputEvent(OutputEvent dto) {
        Optional<EventActionOutputType> opType = outputTypeRepository.findById(
                dto.getEventActionOutputType().getEventActionOutputId());

        if (opType.isPresent()) {
            EventActionOutputType type = opType.get();
            if (checkValidEvent(dto.getTimerDuration(), type.getTimerEnabled())) {
                return Optional.of(outputEventRepository.save(
                        new OutputEvent(null, dto.getTimerDuration(), type)));
            }
        }
        return Optional.empty();
    }
}
