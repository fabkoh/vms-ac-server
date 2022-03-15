package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.AccessGroupSchedule;
import com.vmsac.vmsacserver.model.AccessGroupScheduleDto;
import com.vmsac.vmsacserver.model.CreateAccessGroupScheduleDto;
import com.vmsac.vmsacserver.repository.PersonRepository;
import com.vmsac.vmsacserver.service.AccessGroupScheduleService;
import com.vmsac.vmsacserver.service.AccessGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AccessGroupScheduleController {

    @Autowired
    AccessGroupScheduleService accessGroupScheduleService;

    @GetMapping(path = "/access-group-schedule/{id}")
    public ResponseEntity<?> getById(@PathVariable("id")Long accGrpSchedId){
        Optional<AccessGroupSchedule> optionalAccGrpSched = accessGroupScheduleService.findById(accGrpSchedId);
        if(optionalAccGrpSched.isEmpty()){
            return new ResponseEntity<>("accessGroupSchedule not found",HttpStatus.NOT_FOUND);
        }
        AccessGroupScheduleDto accGrpSchedDto = optionalAccGrpSched.get().toDto();
        return ResponseEntity.ok(accGrpSchedDto);
    }

    @PostMapping(path = "/access-group-schedule/create")
    public ResponseEntity<?> createAccessGroupSchedule(@RequestBody CreateAccessGroupScheduleDto createDto){
        if(createDto.getAccessGroupScheduleName()==null || createDto.getRrule()==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
     return new ResponseEntity<>(accessGroupScheduleService.create(createDto).toDto(),HttpStatus.CREATED);
    }

    @PutMapping(path = "/access-group-schedule/replace")
    public ResponseEntity<?> replaceAccessGroupSchedule(@RequestBody AccessGroupScheduleDto accGrpSchedDto){

        if(accGrpSchedDto.getAccessGroupScheduleName()==null || accGrpSchedDto.getRrule()==null||accGrpSchedDto.getAccessGroupScheduleId()==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(accessGroupScheduleService.save(accGrpSchedDto),HttpStatus.OK);
    }
    @DeleteMapping(path = "/access-group-schedule/{id}")
    public ResponseEntity<?> delete(@PathVariable("id")Long accGrpSchedId){
        Optional<AccessGroupSchedule> optionalAccGrpSched = accessGroupScheduleService.findById(accGrpSchedId);
        if(optionalAccGrpSched.isEmpty()){
            return new ResponseEntity<>("accessGroupScheduleId not found",HttpStatus.NOT_FOUND);
        }
        AccessGroupSchedule deletesched = optionalAccGrpSched.get();
        deletesched.setDeleted(true);
        accessGroupScheduleService.delete(deletesched);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
