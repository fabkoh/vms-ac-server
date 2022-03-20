package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoNDto;
import com.vmsac.vmsacserver.service.AccessGroupEntranceService;
import com.vmsac.vmsacserver.service.EntranceService;
import com.vmsac.vmsacserver.service.AccessGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Access;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class EntranceController {

    @Autowired
    AccessGroupService accessGroupService;
    @Autowired
    EntranceService entranceService;
    @Autowired
    AccessGroupEntranceService accessGroupEntranceService;

    //returns all entrances
    @GetMapping("/entrances")
    public List<EntranceDto> getEntrances(){
        return entranceService.findAllEntrances();
    }

    //returns details of an entrance
    @GetMapping("/entrance/{id}")
    public ResponseEntity<?> getEntrance(@PathVariable("id") Long entranceId){
        Optional<Entrance> optionalEntrance = entranceService.findById(entranceId);
        if(optionalEntrance.isPresent()){
            EntranceDto entranceDto = optionalEntrance.get().toDto();
            return ResponseEntity.ok(entranceDto);
        }
        Map<String, String> errors = new HashMap<>();
        errors.put("entranceId", "Entrance with Id " +
                entranceId + " does not exist");

        List<AccessGroupEntranceNtoNDto> accessGroupEntranceNtoN = accessGroupEntranceService.findAllWhereEntranceId(entranceId);

        if(accessGroupEntranceNtoN.contains(entranceId)) {
            //AccessGroupEntranceNtoNDto accGrpDto = accessGroupEntranceNtoN.get().toDto();
            return ResponseEntity.ok(accessGroupEntranceNtoN);
        }

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    //create an entrance
    @PostMapping("/entrance")
    public ResponseEntity<?> createEntrance(@RequestBody CreateEntranceDto newEntranceDto) {
        if (newEntranceDto.getEntranceName() == null) {
            return new ResponseEntity<>(newEntranceDto, HttpStatus.BAD_REQUEST);
        } else if (entranceService.nameInUse(newEntranceDto.getEntranceName())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("entranceName", "Entrance Name " +
                    newEntranceDto.getEntranceName() + " in use");
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        }
        if(newEntranceDto.getAccessGroupsEntrance() != null){
            Long accessGroupId = newEntranceDto.getAccessGroupsEntrance().getAccessGroup().getAccessGroupId();

            if (accessGroupService.findById(accessGroupId).isEmpty()) {
                Map<Long, String> errors = new HashMap<>();
                errors.put(accessGroupId, "accessGroupId " +
                        accessGroupId + " does not exist");
                return new ResponseEntity<>(errors,HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(entranceService.createEntrance(newEntranceDto), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(entranceService.createEntrance(newEntranceDto), HttpStatus.CREATED);
    }

    @PutMapping("/entrance/enable/{entranceId}")
    public ResponseEntity<?> enableEntrance(@PathVariable("entranceId") Long entranceId) {
        try {
            return ResponseEntity.ok(entranceService.updateEntranceIsActiveWithId(true, entranceId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/entrance/unlock/{entranceId}")
    public ResponseEntity<?> disableEntrance(@PathVariable(name = "entranceId") Long entranceId) {
        try {
            return ResponseEntity.ok(entranceService.updateEntranceIsActiveWithId(false, entranceId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Update name or description of entrance
    @PutMapping("/entrance")
    public ResponseEntity<?> updateEntrance(@RequestBody EntranceDto entranceDto){
       Long tempid = entranceDto.getEntranceId();
       Optional<Entrance> checkDto = entranceService.findById(tempid);
       if(checkDto.isEmpty()){
           Map<String, String> errors = new HashMap<>();
           errors.put("entranceId", "entranceId " +
                   tempid + " not found");
           return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
       }
        if (!Objects.equals(entranceDto.getEntranceName(), checkDto.get().getEntranceName())){
            if(entranceService.nameInUse(entranceDto.getEntranceName())){
              Map<String, String> errors = new HashMap<>();
              errors.put("entranceName", "Entrance Name " +
                      entranceDto.getEntranceName() + " in use");
              return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(entranceService.save(entranceDto).toEntranceOnlyDto(),HttpStatus.OK);
        }
        return new ResponseEntity<>(entranceService.save(entranceDto).toEntranceOnlyDto(),HttpStatus.OK);
    }

    /*
    //set delete = true and set accgrp = null for persons.
    @DeleteMapping("/accessgroup/{id}")
    public ResponseEntity<?> deleteAccessGroup(@PathVariable("id")Long id){

        if (accessGroupService.findById(id).isEmpty()){
            Map<Long, String> errors = new HashMap<>();
            errors.put(id, "Access Group id " +
                    id + " does not exist");
            return new ResponseEntity<>(errors,HttpStatus.NOT_FOUND);
        }
        personService.findByAccGrpId(id,false).forEach(person -> person.setAccessGroup(null));
        AccessGroup deleteGroup = accessGroupService.findById(id).get();
        deleteGroup.setDeleted(true);
        accessGroupService.delete((deleteGroup));
        return new ResponseEntity<>(HttpStatus.OK);
    }
*/
}
