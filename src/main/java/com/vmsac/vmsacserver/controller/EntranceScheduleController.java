package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.CreateEntranceScheduleDto;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceScheduleDto;
import com.vmsac.vmsacserver.service.EntranceScheduleService;
import com.vmsac.vmsacserver.util.UniconUpdater;
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

    @Autowired
    UniconUpdater uniconUpdater;

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
        uniconUpdater.updateUnicons();
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
        uniconUpdater.updateUnicons();
        return ResponseEntity.ok(entranceScheduleDtos);
    }

    @DeleteMapping("/entrance-schedule/delete/{scheduleId}")
    public ResponseEntity<?> deleteEntranceSchedule(@PathVariable Long scheduleId){
        try {
            entranceScheduleService.deleteScheduleWithId(scheduleId);
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }
        uniconUpdater.updateUnicons();
        return ResponseEntity.noContent().build();
    }
}
