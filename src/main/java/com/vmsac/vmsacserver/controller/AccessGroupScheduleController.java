package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupScheduleDto;
import com.vmsac.vmsacserver.model.accessgroupschedule.CreateAccessGroupScheduleDto;
import com.vmsac.vmsacserver.security.service.AccessGroupScheduleService;
import com.vmsac.vmsacserver.security.service.AccessGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AccessGroupScheduleController {

    @Autowired
    AccessGroupScheduleService accessGroupScheduleService;

    @Autowired
    AccessGroupService accessGroupService;


    // returns access group schedules
    @GetMapping("/access-group-schedule")
    public List<AccessGroupScheduleDto> getAccessGroupSchedules(@RequestParam(value = "grouptoentranceids", required = false) List<Long> groupToEntranceIds) {
        if (groupToEntranceIds == null) {
            return accessGroupScheduleService.findAll();
        }
        return accessGroupScheduleService.findAllByGroupToEntranceIdIn(groupToEntranceIds);
    }

    // create an AccessGroupSchedule for each id in groupToEntranceIds and saves all
    // @PostMapping("/access-group-schedule") // closed this api
    public ResponseEntity<?> createAccessGroupSchedules(@RequestBody CreateAccessGroupScheduleDto createAccessGroupScheduleDto,
                                                        @RequestParam("grouptoentranceids") List<Long> groupToEntranceIds) {
        try {
            return new ResponseEntity<>(accessGroupScheduleService.createScheduleForEachId(createAccessGroupScheduleDto, groupToEntranceIds), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // deletes all AccessGroupSchedules with id in groupToEntranceIds
    // then adds the list of AccessGroupSchedules from each id in groupToEntranceIds
    @PutMapping("/access-group-schedule/replace")
    public ResponseEntity<?> replaceAccessGroupSchedules(@RequestBody List<CreateAccessGroupScheduleDto> createAccessGroupScheduleDtos,
                                                         @RequestParam("grouptoentranceids") List<Long> groupToEntranceIds) {
        if (createAccessGroupScheduleDtos.isEmpty()) return ResponseEntity.badRequest().build(); // if empty list, would remove all schedules, leading to empty schedules
        List<AccessGroupScheduleDto> accessGroupScheduleDtos;
        try {
            accessGroupScheduleDtos = accessGroupScheduleService.replaceSchedulesForEachId(createAccessGroupScheduleDtos, groupToEntranceIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(accessGroupScheduleDtos);
    }

    // adds the list of access group schedules from each id in group to entrance ids
    @PutMapping("/access-group-schedule/add")
    public ResponseEntity<?> addAccessGroupSchedules(@RequestBody List<CreateAccessGroupScheduleDto> createAccessGroupScheduleDtos,
                                                     @RequestParam("grouptoentranceids") List<Long> groupToEntranceIds) {
        List<AccessGroupScheduleDto> accessGroupScheduleDtos;
        try {
            accessGroupScheduleDtos = accessGroupScheduleService.addSchedulesForEachId(createAccessGroupScheduleDtos, groupToEntranceIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(accessGroupScheduleDtos);
    }

    // deletes schedule with id scheduleId, and deletes accessGroupEntranceNtoN if it is the last schedule
    @DeleteMapping("/access-group-schedule/{scheduleId}")
    public ResponseEntity<?> deleteAccessGroupSchedule(@PathVariable Long scheduleId) {
        try {
            accessGroupScheduleService.deleteScheduleWithId(scheduleId);
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/access-group-schedule/enable/{scheduleId}")
    public ResponseEntity<?> enableAccessGroupSchedule(@PathVariable("scheduleId") Long scheduleId) {
        Optional<AccessGroupSchedule> accessGroupScheduleOptional= accessGroupScheduleService.findById(scheduleId);
        if (!accessGroupScheduleOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        AccessGroupSchedule accessGroupSchedule = accessGroupScheduleOptional.get();
        accessGroupSchedule.setIsActive(true);
        return ResponseEntity.ok(accessGroupScheduleService.save(accessGroupSchedule.toDto()));
    }

    @PutMapping("/access-group-schedule/disable/{scheduleId}")
    public ResponseEntity<?> disableAccessGroupSchedule(@PathVariable("scheduleId") Long scheduleId) {
        Optional<AccessGroupSchedule> accessGroupScheduleOptional= accessGroupScheduleService.findById(scheduleId);
        if (!accessGroupScheduleOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        AccessGroupSchedule accessGroupSchedule = accessGroupScheduleOptional.get();
        accessGroupSchedule.setIsActive(false);
        return ResponseEntity.ok(accessGroupScheduleService.save(accessGroupSchedule.toDto()));
    }

    // get all current status
    @GetMapping(path = "access-group-schedule/current")
    public ResponseEntity<?> GetAllAccessGroupCurrentStatus() {
        return new ResponseEntity<>(accessGroupScheduleService.GetAllAccessGroupCurrentStatus(),HttpStatus.OK);

    }

    @GetMapping(path = "access-group-schedule/current-entrance/{entranceId}")
    public ResponseEntity<?> GetAllAccessGroupCurrentStatusForOneEntrance(@PathVariable Long entranceId) {
        return new ResponseEntity<>(accessGroupScheduleService.GetAllAccessGroupCurrentStatusForOneEntrance(entranceId),HttpStatus.OK);
    }

    @GetMapping(path = "access-group-schedule/current-person/{personId}")
    public ResponseEntity<?> GetAllAccessGroupCurrentStatusForOnePerson(@PathVariable Long personId) {
        return new ResponseEntity<>(accessGroupScheduleService.GetAllAccessGroupCurrentStatusForOnePerson(personId),HttpStatus.OK);
    }
    // get single current status
    @GetMapping(path = "access-group-schedule/current/{accessGroupId}")
    public ResponseEntity<?> GetAccessGroupCurrentStatus( @PathVariable Long accessGroupId) {

        if (accessGroupService.findById(accessGroupId).get() != null ) {
            return new ResponseEntity<>(accessGroupScheduleService.GetAccessGroupCurrentStatus(accessGroupId), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
