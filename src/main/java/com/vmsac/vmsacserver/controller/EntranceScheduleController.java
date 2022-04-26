package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.CreateEntranceScheduleDto;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceScheduleDto;
import com.vmsac.vmsacserver.service.EntranceScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class EntranceScheduleController {

    @Autowired
    EntranceScheduleService entranceScheduleService;

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
        try {
            return ResponseEntity.ok(entranceScheduleService.replaceSchedulesForEachId(createEntranceScheduleDtos, entranceIds));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/entrance-schedule/add")
    public ResponseEntity<?> addEntranceSchedule(@RequestBody List<CreateEntranceScheduleDto> createEntranceScheduleDtos,
                                                 @RequestParam(value = "entranceids")List<Long> entranceIds){
        try {
            return ResponseEntity.ok(entranceScheduleService.addSchedulesForEachId(createEntranceScheduleDtos, entranceIds));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/entrance-schedule/delete/{scheduleId}")
    public ResponseEntity<?> deleteEntranceSchedule(@PathVariable Long scheduleId){
        try {
            entranceScheduleService.deleteScheduleWithId(scheduleId);
            return ResponseEntity.noContent().build();
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
