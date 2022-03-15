package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AccessGroupSchedule;
import com.vmsac.vmsacserver.model.AccessGroupScheduleDto;
import com.vmsac.vmsacserver.model.CreateAccessGroupDto;
import com.vmsac.vmsacserver.model.CreateAccessGroupScheduleDto;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.AccessGroupScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccessGroupScheduleService {
    @Autowired
    AccessGroupScheduleRepository accessGroupScheduleRepository;

    //Get access group schedule by Id
    public Optional<AccessGroupSchedule> findById(Long Id){
        return accessGroupScheduleRepository.findByAccessGroupScheduleIdAndDeleted(Id,false);
    }

    //create an access group schedule
    public AccessGroupSchedule create(CreateAccessGroupScheduleDto createDto){
        return accessGroupScheduleRepository.save(createDto.toAccessGroupSchedule(false));
    }

    //save an access group schedule
    public AccessGroupSchedule save(AccessGroupScheduleDto accGrpSchedDto){
        return accessGroupScheduleRepository.save(accGrpSchedDto.toAccessGroupSchedule(false));
    }

    //delete access group schedule
    public AccessGroupSchedule delete(AccessGroupSchedule accGrpSched){
        return accessGroupScheduleRepository.save(accGrpSched);
    }
}
