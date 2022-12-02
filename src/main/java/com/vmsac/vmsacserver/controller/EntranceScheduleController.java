package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.CreateEntranceScheduleDto;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceSchedule;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceScheduleDto;
import com.vmsac.vmsacserver.security.service.EntranceScheduleService;
import com.vmsac.vmsacserver.security.service.EntranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class EntranceScheduleController {

    @Autowired
    EntranceScheduleService entranceScheduleService;

    @Autowired
    EntranceService entranceService;


    @GetMapping("/entrance-schedule")
    public List<EntranceScheduleDto> getEntranceSchedules(@RequestParam(value = "entranceids",required = false)List<Long> entranceIds) {
        if (entranceIds==null){
            return entranceScheduleService.findAll();
        }
        return entranceScheduleService.findAllByEntranceIdIn(entranceIds);
    }

    @PutMapping("/entrance-schedule/replace")
    public ResponseEntity<?> replaceEntranceSchedule(@RequestBody List<CreateEntranceScheduleDto> createEntranceScheduleDtos,
                                                     @RequestParam(value = "entranceids")List<Long>entranceIds){
        if (createEntranceScheduleDtos.isEmpty()) return ResponseEntity.badRequest().build(); // if list is empty, would left with no schedules
        List<EntranceScheduleDto> entranceScheduleDtos;
        try {
            entranceScheduleDtos = entranceScheduleService.replaceSchedulesForEachId(createEntranceScheduleDtos, entranceIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(entranceScheduleDtos);
    }

    @PutMapping("/entrance-schedule/add")
    public ResponseEntity<?> addEntranceSchedule(@RequestBody List<CreateEntranceScheduleDto> createEntranceScheduleDtos,
                                                 @RequestParam(value = "entranceids")List<Long> entranceIds){
        List<EntranceScheduleDto> entranceScheduleDtos;
        try {
            entranceScheduleDtos = entranceScheduleService.addSchedulesForEachId(createEntranceScheduleDtos, entranceIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(entranceScheduleDtos);
    }

    @PutMapping("/entrance-schedule/enable/{scheduleId}")
    public ResponseEntity<?> enableEntranceSchedule(@PathVariable("scheduleId") Long scheduleId) {
        EntranceSchedule entranceSchedule = entranceScheduleService.findByScheduleIdAndDeletedFalse(scheduleId);
        if (entranceSchedule == null) {
            return ResponseEntity.notFound().build();
        }
        entranceSchedule.setIsActive(true);
        return ResponseEntity.ok(entranceScheduleService.save(entranceSchedule));
    }

    @PutMapping("/entrance-schedule/disable/{scheduleId}")
    public ResponseEntity<?> disableEntranceSchedule(@PathVariable("scheduleId") Long scheduleId) {
        EntranceSchedule entranceSchedule = entranceScheduleService.findByScheduleIdAndDeletedFalse(scheduleId);
        if (entranceSchedule == null) {
            return ResponseEntity.notFound().build();
        }
        entranceSchedule.setIsActive(false);
        return ResponseEntity.ok(entranceScheduleService.save(entranceSchedule));
    }

    @DeleteMapping("/entrance-schedule/delete/{scheduleId}")
    public ResponseEntity<?> deleteEntranceSchedule(@PathVariable Long scheduleId){
        try {
            entranceScheduleService.deleteScheduleWithId(scheduleId);
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // get all current status
    @GetMapping(path = "entrance-schedule/current")
    public ResponseEntity<?> GetAllEntrancesCurrentStatus() {
        return new ResponseEntity<>(entranceScheduleService.GetAllEntrancesCurrentStatus(),HttpStatus.OK);

    }

    // get single current status
    @GetMapping(path = "entrance-schedule/current/{entranceId}")
    public ResponseEntity<?> GetEntranceCurrentStatus( @PathVariable Long entranceId) {

        if (entranceService.findById(entranceId).get() != null ) {
            return new ResponseEntity<>(entranceScheduleService.GetEntranceCurrentStatus(entranceId), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
