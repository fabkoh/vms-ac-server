package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.service.EntranceService;
import com.vmsac.vmsacserver.service.AccessGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Access;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class EntranceController {

    @Autowired
    AccessGroupService accessGroupService;
    @Autowired
    EntranceService entranceService;


    //returns all entrances
    @GetMapping("/entrances")
    public List<EntranceDto> getEntrances(){
        return entranceService.findAllEntrances();
    }
/*
    //returns details of an accessgroup
    @GetMapping("/accessgroup/{id}")
    public ResponseEntity<?> getAccessGroup(@PathVariable("id") Long accessGroupId){
        Optional<AccessGroup> optionalAccessGroup = accessGroupService.findById(accessGroupId);
        if(optionalAccessGroup.isPresent()){
            AccessGroupDto accessGroupDto = optionalAccessGroup.get().toDto();
            return ResponseEntity.ok(accessGroupDto);
        }
        Map<String, String> errors = new HashMap<>();
        errors.put("accessGroupId", "Access Group with Id " +
                accessGroupId + " does not exist");

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    } */

    //create an entrance
    @PostMapping("/entrance")
    public ResponseEntity<?> createEntrance(@RequestBody CreateEntranceDto entranceDto) {
        if (entranceDto.getEntranceName() == null) {
            return new ResponseEntity<>(entranceDto, HttpStatus.BAD_REQUEST);
        } else if (entranceService.nameInUse(entranceDto.getEntranceName())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("entranceName", "Entrance Name " +
                    entranceDto.getEntranceName() + " in use");
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        }
      /*  if(entranceDto.getAccessGroups() != null){
            List<AccessGroupOnlyDto> stagedAccessGroups = entranceDto.getAccessGroups();
            List<AccessGroup> accessGroups = entranceService.accessGroupList(stagedAccessGroups);
            if (stagedPersons.size() != persons.size()) { // deleted / not found persons
                return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
            }
            AccessGroup createdAccessGroup = accessGroupService.save(accessGroupDto.toAccessGroup(false).toDto());
            persons.forEach(person -> person.setAccessGroup(createdAccessGroup));
            createdAccessGroup.setPersons(persons);
            persons.forEach(person -> personService.save(person.toDto(),false));
            return new ResponseEntity<>(createdAccessGroup.toAccessGroupOnlyDto(), HttpStatus.CREATED);

        } */
        return new ResponseEntity<>(entranceService.createEntrance(entranceDto), HttpStatus.CREATED);
    }

    /*
    //Update name or description of access group
    @PutMapping("/accessgroup")
    public ResponseEntity<?> updateAccessGroup(@RequestBody AccessGroupDto accessGroupDto){
       Long tempid = accessGroupDto.getAccessGroupId();
       Optional<AccessGroup> checkDto = accessGroupService.findById(tempid);
       if(checkDto.isEmpty()){
           Map<String, String> errors = new HashMap<>();
           errors.put("accessGroupId", "accessGroupId " +
                   tempid + " not found");
           return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
       }
        if (!Objects.equals(accessGroupDto.getAccessGroupName(), checkDto.get().getAccessGroupName())){
            if(accessGroupService.nameInUse(accessGroupDto.getAccessGroupName())){
          Map<String, String> errors = new HashMap<>();
          errors.put("accessGroupName", "Access Group Name " +
                  accessGroupDto.getAccessGroupName() + " in use");
          return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
      }
            if(accessGroupDto.getPersons()!= null){
                List<PersonOnlyDto> stagedPersons = accessGroupDto.getPersons();
                List<Person> persons = personService.personsList(stagedPersons);
                if (stagedPersons.size() != persons.size()) { // deleted / not found persons
                    return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
                }
                personService.findByAccGrpId(accessGroupDto.getAccessGroupId(), false).forEach(person -> person.setAccessGroup(null));
                AccessGroup newAccessGroup = accessGroupService.save(accessGroupDto);
                newAccessGroup.setPersons(persons);
                persons.forEach(person -> person.setAccessGroup(newAccessGroup));
                persons.forEach(person -> personService.save(person.toDto(),false));
                accessGroupService.save(newAccessGroup.toDto());
                return new ResponseEntity<>(newAccessGroup.toAccessGroupOnlyDto(),HttpStatus.OK);
            }
            return new ResponseEntity<>(accessGroupService.save(accessGroupDto).toAccessGroupOnlyDto(),HttpStatus.OK);
        }
        if(accessGroupDto.getPersons()!= null){
            List<PersonOnlyDto> stagedPersons = accessGroupDto.getPersons();
            List<Person> persons = personService.personsList(stagedPersons);
            if (stagedPersons.size() != persons.size()) { // deleted / not found persons
                return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
            }
            //remove all existing people first
            personService.findByAccGrpId(checkDto.get().getAccessGroupId(), false).forEach(person -> person.setAccessGroup(null));
            AccessGroup newAccessGroup = accessGroupService.save(checkDto.get().toDto());
            newAccessGroup.setPersons(persons);
            persons.forEach(person -> person.setAccessGroup(newAccessGroup));
            persons.forEach(person -> personService.save(person.toDto(),false));
            accessGroupService.save(newAccessGroup.toDto());
            return new ResponseEntity<>(newAccessGroup.toAccessGroupOnlyDto(),HttpStatus.OK);
        }
//
        return new ResponseEntity<>(accessGroupService.save(accessGroupDto).toAccessGroupOnlyDto(),HttpStatus.OK);
    }

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
