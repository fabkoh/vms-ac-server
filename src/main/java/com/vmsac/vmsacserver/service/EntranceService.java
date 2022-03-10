package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.EntranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntranceService {

    @Autowired
    AccessGroupRepository AccessGroupRepository;

    @Autowired
    EntranceRepository EntranceRepository;

    //read methods
    //returns all undeleted entrances
    public List<EntranceDto> findAllEntrances(){
        return EntranceRepository.findByDeleted(false).stream()
                .map(Entrance::toDto)
                .collect(Collectors.toList());
    }
    public Boolean nameInUse(String name){
        return EntranceRepository.findByEntranceNameAndDeleted(name ,false).isPresent();
    }

    //returns queried entrance
    public Optional<Entrance> findById(Long Id){
        return EntranceRepository.findByEntranceIdAndDeleted(Id,false);
    }

    //create entrance
    public EntranceDto createEntrance(CreateEntranceDto EntranceDto){
        return EntranceRepository.save(EntranceDto.toEntrance(false)).toDto();
    }

    //update access group
    public Entrance save(EntranceDto entranceDto){
        return EntranceRepository.save(entranceDto.toEntrance(false));
    }
//    //delete access group
    //public AccessGroup delete(AccessGroup accessGroup){
    //    return AccessGroupRepository.save(accessGroup);
    //}

}
