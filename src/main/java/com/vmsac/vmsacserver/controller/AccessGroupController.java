package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.service.AccessGroupService;
import com.vmsac.vmsacserver.service.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AccessGroupController {

    @Autowired
    AccessGroupService accessGroupService;
    @Autowired
    PersonService personService;

    //returns all accessgroups
    @GetMapping("/accessgroups")
    public List<AccessGroupDto> getAccessGroups(){
        return accessGroupService.findAllAccessGroups();
    }

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
    }

    @PutMapping("/accessgroup/{accessGroupId}/activate")
    public ResponseEntity<?> activateAccessGroupWithId(@PathVariable Long accessGroupId) {
        AccessGroupDto accessGroup;
        try {
            accessGroup = accessGroupService.activateAccessGroupWithId(accessGroupId);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(accessGroup);
    }

    @PutMapping("/accessgroup/{accessGroupId}/deactivate")
    public ResponseEntity<?> deactivateAccessGroupWithId(@PathVariable Long accessGroupId) {
        AccessGroupDto accessGroup;
        try {
            accessGroup = accessGroupService.deactivateAccessGroupWithId(accessGroupId);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(accessGroup);
    }

    //create an access group
    @PostMapping("/accessgroup")
    public ResponseEntity<?> createAccessGroup(@RequestBody CreateAccessGroupDto accessGroupDto) {
        if (accessGroupDto.getAccessGroupName() == null) {
            return new ResponseEntity<>(accessGroupDto, HttpStatus.BAD_REQUEST);
        } else if (accessGroupService.nameInUse(accessGroupDto.getAccessGroupName())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("accessGroupName", "Access Group Name " +
                    accessGroupDto.getAccessGroupName() + " in use");
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        }
        if(accessGroupDto.getPersons() != null){
            List<PersonOnlyDto> stagedPersons = accessGroupDto.getPersons();
            List<Person> persons = personService.personsList(stagedPersons);
            if (stagedPersons.size() != persons.size()) { // deleted / not found persons
                return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
            }
            AccessGroup createdAccessGroup = accessGroupService.save(accessGroupDto.toAccessGroup(false).toDto());
            persons.forEach(person -> person.setAccessGroup(createdAccessGroup));
            createdAccessGroup.setPersons(persons);
            persons.forEach(person -> personService.save(person.toDto(),false));
            return new ResponseEntity<>(createdAccessGroup.toAccessGroupOnlyDto(), HttpStatus.CREATED);

        }
        AccessGroupDto accessGroup = accessGroupService.createAccessGroup(accessGroupDto);
        return new ResponseEntity<>(accessGroup, HttpStatus.CREATED);
    }

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
            AccessGroupOnlyDto accessGroup = accessGroupService.save(accessGroupDto).toAccessGroupOnlyDto();

            return new ResponseEntity<>(accessGroup,HttpStatus.OK);
        }
        if(accessGroupDto.getPersons()!= null){
            List<PersonOnlyDto> stagedPersons = accessGroupDto.getPersons();
            List<Person> persons = personService.personsList(stagedPersons);
            if (stagedPersons.size() != persons.size()) { // deleted / not found persons
                return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
            }
            //remove all existing people first
            personService.findByAccGrpId(checkDto.get().getAccessGroupId(), false).forEach(person -> person.setAccessGroup(null));
            AccessGroup newAccessGroup = accessGroupService.save(accessGroupDto);
            newAccessGroup.setPersons(persons);
            persons.forEach(person -> person.setAccessGroup(newAccessGroup));
            persons.forEach(person -> personService.save(person.toDto(),false));
            accessGroupService.save(newAccessGroup.toDto());

            return new ResponseEntity<>(newAccessGroup.toAccessGroupOnlyDto(),HttpStatus.OK);
        }
        AccessGroupOnlyDto accessGroup = accessGroupService.save(accessGroupDto).toAccessGroupOnlyDto();

        return new ResponseEntity<>(accessGroup,HttpStatus.OK);
    }

    //set delete = true and set accgrp = null for persons.
    @DeleteMapping("/accessgroup/{id}")
    public ResponseEntity<?> deleteAccessGroup(@PathVariable("id")Long id){
        try {
            accessGroupService.deleteAccessGroupById(id);
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

}
