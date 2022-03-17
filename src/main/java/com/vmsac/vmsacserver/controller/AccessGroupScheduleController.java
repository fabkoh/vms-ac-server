package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupScheduleDto;
import com.vmsac.vmsacserver.model.accessgroupschedule.CreateAccessGroupScheduleDto;
import com.vmsac.vmsacserver.service.AccessGroupScheduleService;
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

    // returns access group schedules
    @GetMapping("/access-group-schedule")
    public List<AccessGroupScheduleDto> getAccessGroupSchedules(@RequestParam(value = "grouptoentranceids", required = false) List<Long> groupToEntranceIds) {
        if (groupToEntranceIds == null) {
            return accessGroupScheduleService.findAll();
        }
        return accessGroupScheduleService.findAllByGroupToEntranceIdIn(groupToEntranceIds);
    }

    // create an AccessGroupSchedule for each id in groupToEntranceIds and saves all
    @PostMapping("/access-group-schedule")
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
        try {
            return ResponseEntity.ok(accessGroupScheduleService.replaceSchedulesForEachId(createAccessGroupScheduleDtos, groupToEntranceIds));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // adds the list of access group schedules from each id in group to entrance ids
    @PutMapping("/access-group-schedule/add")
    public ResponseEntity<?> addAccessGroupSchedules(@RequestBody List<CreateAccessGroupScheduleDto> createAccessGroupScheduleDtos,
                                                     @RequestParam("grouptoentranceids") List<Long> groupToEntranceIds) {
        try {
            return ResponseEntity.ok(accessGroupScheduleService.addSchedulesForEachId(createAccessGroupScheduleDtos, groupToEntranceIds));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // deletes schedule with id scheduleId, and deletes accessGroupEntranceNtoN if it is the last schedule
    @DeleteMapping("/access-group-schedule/{scheduleId}")
    public ResponseEntity<?> deleteAccessGroupSchedule(@PathVariable Long scheduleId) {
        try {
            accessGroupScheduleService.deleteScheduleWithId(scheduleId);
            return ResponseEntity.noContent().build();
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
