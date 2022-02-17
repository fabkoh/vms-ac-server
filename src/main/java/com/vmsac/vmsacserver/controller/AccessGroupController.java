package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.AccessGroupDto;
import com.vmsac.vmsacserver.model.CreateAccessGroupDto;
import com.vmsac.vmsacserver.service.AccessGroupService;
import com.vmsac.vmsacserver.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AccessGroupController {

    @Autowired
    AccessGroupService AccessGroupService;
    @Autowired
    PersonService PersonService;

    //returns all accessgroups
    @GetMapping("/accessgroups")
    public List<AccessGroupDto> getAccessGroups(){
        return AccessGroupService.findAllAccessGroups();
    }

    @GetMapping("/accessgroup/{id}")
    public ResponseEntity<?> getAccessGroup(@PathVariable("id") Long accessGroupId){
        Optional<AccessGroup> optionalAccessGroup = AccessGroupService.findById(accessGroupId);
        if(optionalAccessGroup.isPresent()){
            return ResponseEntity.ok(optionalAccessGroup.get().toDto());
        }
        Map<String, String> errors = new HashMap<>();
        errors.put("accessGroupId", "Access Group with Id " +
                accessGroupId + " does not exist");

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    //create an access group
    @PostMapping("/accessgroup")
    public ResponseEntity<?> createAccessGroup(@RequestBody CreateAccessGroupDto accessGroupDto) {
        if (accessGroupDto.getAccessGroupName() == null) {
            return new ResponseEntity<>(accessGroupDto, HttpStatus.BAD_REQUEST);
        } else if (AccessGroupService.nameInUse(accessGroupDto.getAccessGroupName())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("accessGroupDto.getAccessGroupName", "Access Group Name " +
                    accessGroupDto.getAccessGroupName() + " in use");
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(AccessGroupService.createAccessGroup(accessGroupDto), HttpStatus.CREATED);
    }

    //Update name or description of access group
    @PutMapping("/accessgroup")
    public ResponseEntity<?> updateAccessGroup(@RequestBody AccessGroupDto accessGroupDto){
        if (AccessGroupService.nameInUse(accessGroupDto.getAccessGroupName())){
            Map<String, String> errors = new HashMap<>();
            errors.put("accessGroupDto.getAccessGroupName", "Access Group Name " +
                    accessGroupDto.getAccessGroupName() + " in use");
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        }
        else {
            return new ResponseEntity<>(AccessGroupService.save(accessGroupDto),HttpStatus.OK);
        }
    }

    //delete access group. need to remove persons first.
//    @DeleteMapping("/accessgroup/{id}")
//    public ResponseEntity<?> deleteAccessGroup(@PathVariable("id")AccessGroup accessGroup){
//        if (AccessGroupService.findById(accessGroup.getAccessGroupId()).isPresent()){
//            return new ResponseEntity<>(AccessGroupService.delete(accessGroup),HttpStatus.OK);
//        }
//
//    }

}
