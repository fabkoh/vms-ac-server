package com.vmsac.vmsacserver.controller;


import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodScheduleDto;
import com.vmsac.vmsacserver.model.authmethodschedule.CreateAuthMethodScheduleDto;
import com.vmsac.vmsacserver.service.AuthDeviceService;
import com.vmsac.vmsacserver.service.AuthMethodScheduleService;
import com.vmsac.vmsacserver.util.UniconUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AuthMethodScheduleController {

    @Autowired
    AuthMethodScheduleService authMethodScheduleService;
    @Autowired
    AuthDeviceService authDeviceService;

    @Autowired
    private UniconUpdater uniconUpdater;

    @GetMapping("/authentication-schedule/{authDeviceId}")
    public ResponseEntity<?> getAuthSched(@PathVariable("authDeviceId")Long authDeviceId){
//        System.out.println(authDeviceId);
        if(authDeviceId==null|| authDeviceService.findbyId(authDeviceId).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(authMethodScheduleService.findByDeviceId(authDeviceId), HttpStatus.OK);
    };

    @PutMapping("/authentication-schedule/add")
    public ResponseEntity<?> addAuthMethodSchedules(@RequestBody List<CreateAuthMethodScheduleDto> CreateScheduleList,
                                                    @RequestParam("authDeviceIds")List<Long> authDeviceIdList){
        if(CreateScheduleList.isEmpty()||authDeviceIdList.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
//        List<AuthMethodScheduleDto> createdDtos;
//        try{
        authMethodScheduleService.addAll(CreateScheduleList,authDeviceIdList);
        uniconUpdater.updateUnicons();
        return new ResponseEntity<>(HttpStatus.OK);
//        }catch (Exception e){
//            return ResponseEntity.badRequest().build();
//        }
//        return ResponseEntity.ok(createdDtos);
    }
    @PutMapping("/auth-method-test/") //testing rrule validations, will remove later.
    public ResponseEntity<?> test(@RequestBody List<CreateAuthMethodScheduleDto> CreateScheduleList,
                                  @RequestParam("authDeviceIds")List<Long> authDeviceIdList){

     return new ResponseEntity<>(authMethodScheduleService.checkNewScheds(CreateScheduleList),HttpStatus.OK) ;
    }

    @PutMapping("/authentication-schedule/replace")
    public ResponseEntity<?> replace(@RequestBody List<CreateAuthMethodScheduleDto> CreateScheduleList,
                                     @RequestParam("authDeviceIds")List<Long> authDeviceIdList){

        authMethodScheduleService.replace(CreateScheduleList,authDeviceIdList);
        uniconUpdater.updateUnicons();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/authentication-schedule/{authMethodScheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable("authMethodScheduleId")Long authMethodScheduleId){

        authMethodScheduleService.deleteSched(authMethodScheduleId);
        uniconUpdater.updateUnicons();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
