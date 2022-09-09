package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.CreateEntranceScheduleDto;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceScheduleDto;
import com.vmsac.vmsacserver.service.EntranceScheduleService;
import com.vmsac.vmsacserver.service.EntranceService;
import com.vmsac.vmsacserver.util.UniconUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
