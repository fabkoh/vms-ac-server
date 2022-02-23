package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.AccessGroupDto;
import com.vmsac.vmsacserver.model.CreateAccessGroupDto;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccessGroupService {

    @Autowired
    AccessGroupRepository AccessGroupRepository;

    //read methods
    //returns all undeleted access groups
    public List<AccessGroupDto> findAllAccessGroups(){
        return AccessGroupRepository.findByDeleted(false).stream()
                .map(AccessGroup::toDto)
                .collect(Collectors.toList());
    }
    public Boolean nameInUse(String name ){
        return AccessGroupRepository.findByAccessGroupNameAndDeleted(name ,false).isPresent();
    }

    //returns queried access group
    public Optional<AccessGroup> findById(Long Id){
        return AccessGroupRepository.findByAccessGroupIdAndDeleted(Id,false);
    }

    //create access group
    public AccessGroupDto createAccessGroup(CreateAccessGroupDto AccessGroupDto){
        return AccessGroupRepository.save(AccessGroupDto.toAccessGroup(false)).toDto();
    }

    //update access group
    public AccessGroup save(AccessGroupDto accessGroupDto){
        return AccessGroupRepository.save(accessGroupDto.toAccessGroup(false));
    }
//    //delete access group
    public AccessGroup delete(AccessGroup accessGroup){
        return AccessGroupRepository.save(accessGroup);
    }

}
