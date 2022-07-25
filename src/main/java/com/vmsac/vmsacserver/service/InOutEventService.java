package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.repository.*;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InOutEventService {

    @Autowired
    EventActionInputTypeRepository inputTypeRepository;

    @Autowired
    EventActionOutputTypeRepository outputTypeRepository;

    @Autowired
    InputEventRepository inputEventRepository;

    @Autowired
    OutputEventRepository outputEventRepository;

    @Autowired
    GENConfigsRepository genRepo;

    public InputEvent createInputEvent(InputEvent dto, Long controllerId) throws NotFoundException {
        Optional<EventActionInputType> opType = inputTypeRepository.findById(
                dto.getEventActionInputType().getEventActionInputId());

        if (opType.isPresent()) {
            EventActionInputType type = opType.get();
            if (type.getEventActionInputName().startsWith("GEN_IN")) {
                if (controllerId != null) {
                    GENConfigs g = genRepo.getByController_ControllerIdAndPinName(controllerId,
                            type.getEventActionInputName().substring(7));
                    g.setStatus("IN");
                    genRepo.save(g);
                }
            }
            return inputEventRepository.save(new InputEvent(null,
                    dto.getTimerDuration(), type));
        } else throw new NotFoundException("Input type does not exist");
    }

    public OutputEvent createOutputEvent(OutputEvent dto, Long controllerId) throws NotFoundException {
        Optional<EventActionOutputType> opType = outputTypeRepository.findById(
                dto.getEventActionOutputType().getEventActionOutputId());

        if (opType.isPresent()) {
            EventActionOutputType type = opType.get();
            if (type.getEventActionOutputName().startsWith("GEN_OUT")) {
                if (controllerId != null) {
                    GENConfigs g = genRepo.getByController_ControllerIdAndPinName(controllerId,
                            type.getEventActionOutputName().substring(8));
                    g.setStatus("OUT");
                    genRepo.save(g);
                }
            }
            return outputEventRepository.save(new OutputEvent(null,
                    dto.getTimerDuration(), type));
        }
        else throw new NotFoundException("Output type does not exist");
    }
}
