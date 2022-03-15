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
    EntranceRepository entranceRepository;

    //read methods
    //returns all undeleted entrances
    public List<EntranceDto> findAllEntrances(){
        return entranceRepository.findByDeleted(false).stream()
                .map(Entrance::toDto)
                .collect(Collectors.toList());
    }
    public Boolean nameInUse(String name){
        return entranceRepository.findByEntranceNameAndDeletedFalse(name).isPresent();
    }

    //returns queried entrance
    public Optional<Entrance> findById(Long Id){
        return entranceRepository.findByEntranceIdAndDeletedFalse(Id);
    }

    //create entrance
    public EntranceDto createEntrance(CreateEntranceDto EntranceDto){
        return entranceRepository.save(EntranceDto.toEntrance(false)).toDto();
    }

    //update entrance
    public Entrance save(EntranceDto entranceDto){
        return entranceRepository.save(entranceDto.toEntrance(false));
    }
//    //delete access group
    //public AccessGroup delete(AccessGroup accessGroup){
    //    return AccessGroupRepository.save(accessGroup);
    //}

    // set isActive to true for the given entrance ids
    public EntranceDto updateEntranceIsActiveWithId(Boolean isActive, Long entranceId) throws Exception {
        Entrance entrance = entranceRepository.findByEntranceIdAndDeletedFalse(entranceId).orElseThrow();
        entrance.setIsActive(isActive);
        return entranceRepository.save(entrance).toDto();
    }
}
