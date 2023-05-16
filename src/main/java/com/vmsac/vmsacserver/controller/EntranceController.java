package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoNDto;
import com.vmsac.vmsacserver.service.AccessGroupEntranceService;
import com.vmsac.vmsacserver.service.ControllerService;
import com.vmsac.vmsacserver.service.EntranceService;
import com.vmsac.vmsacserver.service.AccessGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    @Autowired
    ControllerService controllerService;

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
            EntranceDto entranceDto = entranceService.createEntrance(newEntranceDto);

            return new ResponseEntity<>(entranceDto, HttpStatus.CREATED);
        }
        EntranceDto entranceDto = entranceService.createEntrance(newEntranceDto);

        return new ResponseEntity<>(entranceDto, HttpStatus.CREATED);
    }

    @PutMapping("/entrance/enable/{entranceId}")
    public ResponseEntity<?> enableEntrance(@PathVariable("entranceId") Long entranceId) {
        EntranceDto entranceDto;
        try {
            entranceDto = entranceService.updateEntranceIsActiveWithId(true, entranceId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(entranceDto);
    }

    @PutMapping("/entrance/disable/{entranceId}")
    public ResponseEntity<?> disableEntrance(@PathVariable("entranceId") Long entranceId) {
        EntranceDto entranceDto;
        try {
            entranceDto = entranceService.updateEntranceIsActiveWithId(false, entranceId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(entranceDto);
    }

    @GetMapping("/entrance/unlock/{entranceId}")
    public ResponseEntity<?> unlockEntrance(@PathVariable(name = "entranceId") Long entranceId) {
        Optional<Entrance> entranceOptional = entranceService.findById(entranceId);
        if (!entranceOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        try {
            Entrance entrance = entranceOptional.get();
            if (controllerService.unlockEntrance(entrance)){
                return ResponseEntity.ok().build();
            }
            //TODO: CALL PI TO UNLOCK ENTRANCE
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.badRequest().build();
    }

    //Update name or description of entrance
    @PutMapping("/entrance")
    public ResponseEntity<?> updateEntrance(@RequestBody EntranceDto entranceDto){
       Long tempid = entranceDto.getEntranceId();
       Optional<Entrance> checkDto = entranceService.findById(tempid);

       Boolean isActive = entranceService.findById(tempid).get().getIsActive();
       entranceDto.setIsActive(isActive);


       if(checkDto.isEmpty()){
           Map<String, String> errors = new HashMap<>();
           errors.put("entranceId", "entranceId " +
                   tempid + " not found");
           return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
       }
        // set the used of the entrance
        entranceDto.setUsed(checkDto.get().getUsed());

        if (!Objects.equals(entranceDto.getEntranceName(), checkDto.get().getEntranceName())){
            if(entranceService.nameInUse(entranceDto.getEntranceName())){
              Map<String, String> errors = new HashMap<>();
              errors.put("entranceName", "Entrance Name " +
                      entranceDto.getEntranceName() + " in use");
              return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
            }
            EntranceOnlyDto entrance = entranceService.save(entranceDto).toEntranceOnlyDto();

            return new ResponseEntity<>(entrance,HttpStatus.OK);
        }
        EntranceOnlyDto entrance = entranceService.save(entranceDto).toEntranceOnlyDto();

        return new ResponseEntity<>(entrance,HttpStatus.OK);
    }


    //set delete = true and set accgrp = null for persons.
    @DeleteMapping("/entrance/{id}")
    public ResponseEntity<?> deleteEntrance(@PathVariable("id")Long id){
        if (entranceService.findById(id).isEmpty()){
            Map<Long, String> errors = new HashMap<>();
            errors.put(id, "Entrance id " +
                    id + " does not exist");
            return new ResponseEntity<>(errors,HttpStatus.NOT_FOUND);
        }
        try{
            Entrance deleteEntrance = entranceService.findById(id).get();
            deleteEntrance.setDeleted(true);
            entranceService.save(deleteEntrance.toDto());
            //get NtoN table
            entranceService.delete(id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
