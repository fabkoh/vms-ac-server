package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AuthDevice;
import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodScheduleDto;
import com.vmsac.vmsacserver.model.authmethodschedule.CreateAuthMethodScheduleDto;
import com.vmsac.vmsacserver.repository.AuthDeviceRepository;
import com.vmsac.vmsacserver.repository.AuthMethodRepository;
import com.vmsac.vmsacserver.repository.AuthMethodScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Service
public class AuthMethodScheduleService {
    @Autowired
    AuthMethodScheduleRepository authMethodScheduleRepository;
    @Autowired
    AuthMethodRepository authMethodRepository;
    @Autowired
    AuthDeviceRepository authDeviceRepository;


    public List<AuthMethodScheduleDto> findByDeviceId(Long authDeviceId) {
        return authMethodScheduleRepository.findByAuthDevice_AuthDeviceIdAndDeletedFalse(authDeviceId)
                .stream()
                .map(AuthMethodSchedule::toDto)
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> deleteSched(Long authMethodSchedId){
        if(authMethodScheduleRepository.findByAuthMethodScheduleIdAndDeletedFalse(authMethodSchedId).isEmpty()){
            return new ResponseEntity<>("authMethodSchedule not found",HttpStatus.NOT_FOUND);
        }
        else{
            AuthMethodSchedule toDelete = authMethodScheduleRepository.findByAuthMethodScheduleIdAndDeletedFalse(authMethodSchedId)
                    .get();
            toDelete.setDeleted(true);
            authMethodScheduleRepository.save(toDelete);
            return ResponseEntity.ok("Successfully Deleted");
        }
    }
    public ResponseEntity<?>replace(List<CreateAuthMethodScheduleDto> CreateList,
                                   List<Long> authDeviceIds){
        //check for invalid authdevice
        List<AuthDevice>foundAuthDevice = new ArrayList<>();
        authDeviceIds.forEach(aLong -> {
            if(authDeviceRepository.findByAuthDeviceId(aLong).isPresent()){
                foundAuthDevice.add(authDeviceRepository.findByAuthDeviceId(aLong).get());
            }
        });
        if(foundAuthDevice.size()!= authDeviceIds.size()){
            return new ResponseEntity<>("Invalid AuthDevice(s)",HttpStatus.NOT_FOUND);
        }
        //check for invalid authmethod
        List<Long> authMethodIds = CreateList.stream().map(createAuthMethodScheduleDto -> createAuthMethodScheduleDto.getAuthMethod().getAuthMethodId()).collect(Collectors.toList());
        List<AuthMethod> foundAuthMethods = new ArrayList<>();
        authMethodIds.forEach(id->foundAuthMethods.add(authMethodRepository.findById(id).get()));
        if(foundAuthMethods.size()!=authMethodIds.size()){
            System.out.println(foundAuthMethods.size());
            return new ResponseEntity<>("Invalid authMethod(s)",HttpStatus.NOT_FOUND);
        }
        //check if new scheds are valid
//        if(checkNewScheds(CreateList)){
//            return new ResponseEntity<>("Clashes detected in submitted schedules ",HttpStatus.CONFLICT);
//        };
        //removes the old schedules if all checks passed
        authDeviceIds.forEach(aLong ->
                authMethodScheduleRepository.findByAuthDevice_AuthDeviceIdAndDeletedFalse(aLong)
                        .forEach(authMethodSchedule -> {
                            authMethodSchedule.setDeleted(true);
                            authMethodScheduleRepository.save(authMethodSchedule);
                                }
                        ));


        //save the new scheds
        List<AuthMethodSchedule> toCreate = new ArrayList<>();
        for (CreateAuthMethodScheduleDto CreateAuthMethodScheduleDto : CreateList) {
            toCreate.addAll(
                    authDeviceIds.stream()
                            .map((id) -> {
                                CreateAuthMethodScheduleDto.setAuthMethod(authMethodRepository.findById(CreateAuthMethodScheduleDto.getAuthMethod().getAuthMethodId()).get());
                                CreateAuthMethodScheduleDto.setAuthDevice(authDeviceRepository.findByAuthDeviceId(id).get());
                                return CreateAuthMethodScheduleDto.toAuthMethodSchedule();
                            })
                            .collect(Collectors.toList())
            );
        }

        return new ResponseEntity<>(authMethodScheduleRepository.saveAll(toCreate).stream().map(AuthMethodSchedule::toDto ).collect(Collectors.toList()),HttpStatus.OK);
    }

    public ResponseEntity<?>addAll(List<CreateAuthMethodScheduleDto> CreateList,
                                             List<Long> authDeviceIds){
        List<Long> authMethodIds = CreateList.stream().map(createAuthMethodScheduleDto -> createAuthMethodScheduleDto.getAuthMethod().getAuthMethodId()).collect(Collectors.toList());
        List<AuthMethod> foundAuthMethods = new ArrayList<>();
        authMethodIds.forEach(id->foundAuthMethods.add(authMethodRepository.findById(id).get()));
        if(foundAuthMethods.size()!=authMethodIds.size()){
//            System.out.println(foundAuthMethods.size());
            throw new RuntimeException("Invalid AuthMethod(s)"); //check for empty or invalid authMethod
        }

        //gets all authMethodScheds from authDeviceIds
        List<AuthDevice>foundAuthDevice = new ArrayList<>();
        List<AuthMethodScheduleDto> oldScheduleList = new ArrayList<>();
        authDeviceIds.forEach(aLong -> {
            if(authDeviceRepository.findByAuthDeviceId(aLong).isPresent()){
                foundAuthDevice.add(authDeviceRepository.findByAuthDeviceId(aLong).get());
            }
        });
        if(foundAuthDevice.size()!= authDeviceIds.size()){
            System.out.println(foundAuthDevice);
            System.out.println(authDeviceIds.size());
            return new ResponseEntity<>("Invalid AuthDevice(s)",HttpStatus.NOT_FOUND);
        }
        authDeviceIds.forEach(aLong ->
                oldScheduleList.addAll(authMethodScheduleRepository.findByAuthDevice_AuthDeviceIdAndDeletedFalse(aLong)
                        .stream().map(AuthMethodSchedule::toDto).collect(Collectors.toList()))
                );
//        System.out.println(oldScheduleList);

        ////start of rrule checker functions////
//        if(checkNewScheds(CreateList)){  //checks for conflicts in new schedules to create
//            return new ResponseEntity<>("Clashes detected in submitted schedules", HttpStatus.CONFLICT);
//        }

        ////end of rrule checker function////

        List<AuthMethodSchedule> toCreate = new ArrayList<>();
        for (CreateAuthMethodScheduleDto CreateAuthMethodScheduleDto : CreateList) {
            toCreate.addAll(
                    authDeviceIds.stream()
                            .map((id) -> {
                                CreateAuthMethodScheduleDto.setAuthMethod(authMethodRepository.findById(CreateAuthMethodScheduleDto.getAuthMethod().getAuthMethodId()).get());
                                CreateAuthMethodScheduleDto.setAuthDevice(authDeviceRepository.findByAuthDeviceId(id).get());
                                return CreateAuthMethodScheduleDto.toAuthMethodSchedule();
                            })
                            .collect(Collectors.toList())
            );
        }


//        authMethodScheduleRepository.saveAll(toCreate).stream().map(AuthMethodSchedule::toDto ).collect(Collectors.toList());
//        return new ResponseEntity<>(oldScheduleList,HttpStatus.OK);

        return new ResponseEntity<>(authMethodScheduleRepository.saveAll(toCreate).stream().map(AuthMethodSchedule::toDto ).collect(Collectors.toList()),HttpStatus.OK);
    }

    //checks the new scheds and returns true if clashes are detected
    public Boolean checkNewScheds(List<CreateAuthMethodScheduleDto> Createlist){
        List<CreateAuthMethodScheduleDto> cleanedList = convertRruleArray(Createlist);
        List<CreateAuthMethodScheduleDto> clashedlist = new ArrayList<>();

        for (int j = 0; j < cleanedList.size(); j++) {
            for (int k = j+1; k < cleanedList.size(); k++) {
                if(compareRruleArray(cleanedList.get(j).getRruleArray(),cleanedList.get(k).getRruleArray())){
                    //check timestart and time end
                    clashedlist.add(cleanedList.get(j));
                    clashedlist.add(cleanedList.get(k));
                }
//                else{System.out.println("comparerrule returned false,no clashes");}
            }
        }
//        String time = "23:59";
//        System.out.println(time.substring(0,2));
//        System.out.println(time.substring(3,5));
//        LocalTime t1  = LocalTime.parse("23:59");
//        LocalTime t2  = LocalTime.parse("00:59");
//        if(t1.compareTo(t2)>0){
//            System.out.println("t1 is bigger");
//        }
//        else{System.out.println("t1 is smaller");}

        if(clashedlist.size()>0){
            return true;
        }
        else{
            return false;}
    }

    //Compares rruleArray of 2 scheds. returns true if clashes detected.
    public boolean compareRruleArray(String[] arr1, String[] arr2){
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr2.length; j++) {
                if(Objects.equals(arr1[i], arr2[j])){
                    return true; //must check timestart and timeend too
                }
            }
        }
        return false; //if no repeats, returns false.
    }

    //Converts rrule into rruleArray. returns updated list of createDto
    public List<CreateAuthMethodScheduleDto> convertRruleArray(List<CreateAuthMethodScheduleDto> CreateList){
        List<CreateAuthMethodScheduleDto> cleanedList = new ArrayList<>();
        for(int i=0;i<CreateList.size();i++){ //converting rrule to rruleArray for comparison.
            CreateAuthMethodScheduleDto temp = CreateList.get(i);
            temp.setRruleArray(temp.getRrule().substring(36).split(","));
//            temp.setRrule(temp.getRrule().substring(36));
            cleanedList.add(temp);
        }
        return cleanedList;
    }

    public Boolean compareTime(String timestart1,String timeend1, String timestart2 , String timeend2){
        LocalTime ts1  = LocalTime.parse(timestart1);
        LocalTime ts2  = LocalTime.parse(timestart2);
        LocalTime te1  = LocalTime.parse(timeend1);
        LocalTime te2  = LocalTime.parse(timeend2);
        if(ts1.compareTo(te2)>0 && ts2.compareTo(te1)>0){
            return true; //no overlap
        }
        else{return false;} //overlap exists
        //case1 timestart 2 > time end 1

        //case 2 time start 1 > time end 2
    }


}
