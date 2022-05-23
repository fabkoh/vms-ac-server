package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AuthDevice;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.model.accessgroupschedule.CreateAccessGroupScheduleDto;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodScheduleDto;
import com.vmsac.vmsacserver.model.authmethodschedule.CreateAuthMethodScheduleDto;
import com.vmsac.vmsacserver.repository.AuthDeviceRepository;
import com.vmsac.vmsacserver.repository.AuthMethodRepository;
import com.vmsac.vmsacserver.repository.AuthMethodScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthMethodScheduleService {
    @Autowired
    AuthMethodScheduleRepository authMethodScheduleRepository;
    @Autowired
    AuthMethodRepository authMethodRepository;
    @Autowired
    AuthDeviceRepository authDeviceRepository;


    public List<AuthMethodScheduleDto> findByDeviceId(Long authDeviceId) {
        return authMethodScheduleRepository.findByAuthDevice_AuthDeviceId(authDeviceId)
                .stream()
                .map(AuthMethodSchedule::toDto)
                .collect(Collectors.toList());
    }
    public List<AuthMethodScheduleDto>addAll(List<CreateAuthMethodScheduleDto> CreateList,
                                             List<Long> authDeviceIds){
        List<Long> authMethodIds = CreateList.stream().map(createAuthMethodScheduleDto -> createAuthMethodScheduleDto.getAuthMethod().getAuthMethodId()).collect(Collectors.toList());
        if(authMethodRepository.findAllByAuthMethodIdInAndDeletedFalse(authMethodIds).size()!=authMethodIds.size()){
            throw new RuntimeException("Invalid AuthMethod(s)"); //check for empty or invalid authMethod
        }
        //function to check rrule string here.

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
//        List<AuthMethodSchedule> newList = CreateList.stream().map(CreateAuthMethodScheduleDto::toAuthMethodSchedule).collect(Collectors.toList());
//        List<AuthMethodScheduleDto> nextList = newList.stream().map(authMethodSchedule -> authMethodSchedule.toDto()).collect(Collectors.toList());
//        return authMethodScheduleRepository.saveAll(nextList);
        return authMethodScheduleRepository.saveAll(toCreate).stream().map(AuthMethodSchedule::toDto ).collect(Collectors.toList());
    }
}
