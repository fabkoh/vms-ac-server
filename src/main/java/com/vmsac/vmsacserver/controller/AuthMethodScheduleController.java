package com.vmsac.vmsacserver.controller;


import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodScheduleDto;
import com.vmsac.vmsacserver.model.authmethodschedule.CreateAuthMethodScheduleDto;
import com.vmsac.vmsacserver.service.AuthDeviceService;
import com.vmsac.vmsacserver.service.AuthMethodScheduleService;
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

    @GetMapping("/auth-method-schedule/{authDeviceId}")
    public ResponseEntity<?> getAuthSched(@PathVariable("authDeviceId")Long authDeviceId){
//        System.out.println(authDeviceId);
        if(authDeviceId==null|| authDeviceService.findbyId(authDeviceId).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(authMethodScheduleService.findByDeviceId(authDeviceId), HttpStatus.OK);
    };

//    @PutMapping("/auth-method-schedule/add")
//    public ResponseEntity<?> addAuthMethodSchedules(@RequestBody List<CreateAuthMethodScheduleDto> CreateScheduleList,
//                                                    @RequestParam("authDeviceIds")List<Long> authDeviceIdList){
//        if(CreateScheduleList.isEmpty()||authDeviceIdList.isEmpty()){
//            return ResponseEntity.badRequest().build();
//        }
//        List<AuthMethodScheduleDto> createdDtos;
//        try{
//             createdDtos = authMethodScheduleService.addAll(CreateScheduleList,authDeviceIdList);
//        }catch (Exception e){
//            return ResponseEntity.badRequest().build();
//        }
//        return ResponseEntity.ok(createdDtos);
//    }
}
