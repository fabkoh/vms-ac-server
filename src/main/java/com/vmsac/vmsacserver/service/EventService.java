package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.repository.EventActionInputTypeRepository;
import com.vmsac.vmsacserver.repository.EventActionOutputTypeRepository;
import com.vmsac.vmsacserver.repository.InputEventRepository;
import com.vmsac.vmsacserver.repository.OutputEventRepository;
import org.springframework.beans.InvalidPropertyException;
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

    public InputEvent createInputEvent(InputEvent dto) {
        Optional<EventActionInputType> type = inputTypeRepository.findById(
                dto.getEventActionInputType().getEventActionInputId());
        dto.setEventActionInputType(type.get());
        return inputEventRepository.save(dto);
    }

    public OutputEvent createOutputEvent(OutputEvent dto) {
        Optional<EventActionOutputType> type = outputTypeRepository.findById(
                dto.getEventActionOutputType().getEventActionOutputId());
        dto.setEventActionOutputType(type.get());
        return outputEventRepository.save(dto);
    }
}
