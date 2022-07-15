package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.EventActionInputType;
import com.vmsac.vmsacserver.model.EventActionOutputType;
import com.vmsac.vmsacserver.model.InputEvent;
import com.vmsac.vmsacserver.model.OutputEvent;
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
        return inputEventRepository.save(dto);
    }

    public OutputEvent createOutputEvent(OutputEvent dto) {
        return outputEventRepository.save(dto);
    }
}
