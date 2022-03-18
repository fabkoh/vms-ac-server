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

            List<AccessGroupEntranceNtoNDto> accessGroups = accessGroupEntranceService.findAllWhereAccessGroupId(accessGroupId);
            //List<AccessGroupEntranceNtoN> newAccessGroups =
            //Entrance createdEntrance = entranceService.save(newEntranceDto.toEntrance(false).toDto());

            //newAccessGroups.forEach(accessGroupEntranceNtoNDto -> accessGroupEntranceNtoNDto.setEntrance(createdEntrance));

       //     accessGroupEntranceService.assignAccessGroupsToEntrance(accessGroupId);

           // newAccessGroups.forEach(accGrp -> accGrp.setAccessGroup(newAccessGroups));
           // newEntranceDto.setAccessGroupsEntrance(newAccessGroups);
            return new ResponseEntity<>(entranceService.createEntrance(newEntranceDto), HttpStatus.CREATED);

            //creating entrance logic
            //Entrance createdEntrance = entranceService.save(entranceDto.toEntrance(false).toDto());
           // accessGroupsEntrances.forEach(accessGroupNtoN -> accessGroupNtoN.setEntrance(createdEntrance));
           // createdEntrance.setAccessGroupEntrance(accessGroupsEntrances);

            //linking access group to entrance logic
            /*AccessGroup createdAccessGroup = accessGroupService.save(accessGroupDto.toAccessGroup(false).toDto());
            accessGroupsEntrances.forEach(accessGroupNtoN -> accessGroupNtoN.setAccessGroup(createdAccessGroup));
            return new ResponseEntity<>(createdEntrance.toEntranceOnlyDto(), HttpStatus.CREATED);*/

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
    public ResponseEntity<?> updateEntrance(@RequestBody EntranceDto entranceDto, @RequestBody AccessGroupEntranceNtoNDto accGroupEntranceDto){
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
            //if this entrance id is in AccGrpEntranceNtoN table
          /*  if(Objects.equals(accGroupEntranceDto.getEntrance().getEntranceId(), tempid)){
                //accessGroupEntranceService.assignAccessGroupToEntrances();


                personService.findByAccGrpId(accessGroupDto.getAccessGroupId(), false).forEach(person -> person.setAccessGroup(null));
                AccessGroup newAccessGroup = accessGroupService.save(accessGroupDto);
                newAccessGroup.setPersons(persons);
                persons.forEach(person -> person.setAccessGroup(newAccessGroup));
                persons.forEach(person -> personService.save(person.toDto(),false));
                accessGroupService.save(newAccessGroup.toDto());
                return new ResponseEntity<>(newAccessGroup.toAccessGroupOnlyDto(),HttpStatus.OK);
            } */
            return new ResponseEntity<>(entranceService.save(entranceDto).toEntranceOnlyDto(),HttpStatus.OK);
        }
      /*  if(accessGroupDto.getPersons()!= null){
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
        } */
//
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