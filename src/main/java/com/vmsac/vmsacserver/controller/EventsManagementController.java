package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.EventActionInputType;
import com.vmsac.vmsacserver.model.EventActionInputTypeCreateDto;
import com.vmsac.vmsacserver.repository.EventActionInputTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class EventsManagementController {

    @Autowired
    EventActionInputTypeRepository inputTypeRepository;

    // POST EventActionInputType
    @PostMapping("/event/input/type")
    public ResponseEntity<?> postInputType(@RequestBody EventActionInputTypeCreateDto dto) {
        EventActionInputType inputType = inputTypeRepository.save(new EventActionInputType(
                null, dto.getEventActionInputTypeName(), dto.getTimerEnabled(),
                dto.getEventActionInputTypeConfig())
        );

        return new ResponseEntity<>(inputType, HttpStatus.CREATED);
    }

    // PUT EventActionInputType
    @PutMapping("/event/input/type/{id}")
    public ResponseEntity<?> putInputType(@RequestBody EventActionInputType dto,
                                          @PathVariable Long id) {
        Optional<EventActionInputType> toEdit = inputTypeRepository.findById(id);
        if (toEdit.isPresent()) {
            EventActionInputType type = toEdit.get();
            type.setEventActionInputTypeName(dto.getEventActionInputTypeName());
            type.setTimerEnabled(dto.getTimerEnabled());
            type.setEventActionInputTypeConfig(dto.getEventActionInputTypeConfig());
            type = inputTypeRepository.save(type);
            return new ResponseEntity<>(type, HttpStatus.OK);
        } else return ResponseEntity.notFound().build();
    }

    // DELETE EventActionInputType
    @DeleteMapping("/event/input/type/{id}")
    public ResponseEntity<?> deleteInputType(@PathVariable Long id) {
        Optional<EventActionInputType> toEdit = inputTypeRepository.findById(id);
        if (toEdit.isPresent()) {
            inputTypeRepository.delete(toEdit.get());
            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }
}
