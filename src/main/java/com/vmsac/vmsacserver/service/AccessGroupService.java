package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.AccessGroupScheduleRepository;
import com.vmsac.vmsacserver.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccessGroupService {

    @Autowired
    AccessGroupRepository accessGroupRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    AccessGroupEntranceNtoNRepository accessGroupEntranceNtoNRepository;

    @Autowired
    AccessGroupScheduleRepository accessGroupScheduleRepository;

    //read methods
    //returns all undeleted access groups
    public List<AccessGroupDto> findAllAccessGroups(){
        return accessGroupRepository.findByDeleted(false).stream()
                .map(AccessGroup::toDto)
                .collect(Collectors.toList());
    }
    public Boolean nameInUse(String name ){
        return accessGroupRepository.findByAccessGroupNameAndDeleted(name ,false).isPresent();
    }

    //returns queried access group
    public Optional<AccessGroup> findById(Long Id){
        return accessGroupRepository.findByAccessGroupIdAndDeleted(Id,false);
    }

    //create access group
    public AccessGroupDto createAccessGroup(CreateAccessGroupDto AccessGroupDto){
        return accessGroupRepository.save(AccessGroupDto.toAccessGroup(false)).toDto();
    }

    public AccessGroupDto activateAccessGroupWithId(Long accessGroupId) throws Exception{
        Optional<AccessGroup> accessGroup = accessGroupRepository.findByAccessGroupIdAndDeleted(accessGroupId,false);
        if (!accessGroup.isPresent()) throw new RuntimeException("Access Group does not exist");

        AccessGroup toActivate = accessGroup.get();
        toActivate.setIsActive(true);
        return accessGroupRepository.save(toActivate).toDto();
    }

    public AccessGroupDto deactivateAccessGroupWithId(Long accessGroupId) throws Exception{
        Optional<AccessGroup> accessGroup = accessGroupRepository.findByAccessGroupIdAndDeleted(accessGroupId,false);
        if (!accessGroup.isPresent()) throw new RuntimeException("Access Group does not exist");

        AccessGroup toActivate = accessGroup.get();
        toActivate.setIsActive(false);
        return accessGroupRepository.save(toActivate).toDto();
    }

    //update access group
    public AccessGroup save(AccessGroupDto accessGroupDto){
        return accessGroupRepository.save(accessGroupDto.toAccessGroup(false));
    }
//    //delete access group
    public AccessGroup delete(AccessGroup accessGroup){
        return accessGroupRepository.save(accessGroup);
    }

    // delete access group
    // delete access group id from person
    // delete access group entrance n to n
    // delete access group schedules
    public void deleteAccessGroupById(Long accessGroupId) throws Exception {
        AccessGroup groupToDelete =
                accessGroupRepository.findByAccessGroupIdAndDeletedFalse(accessGroupId).orElseThrow(() -> new RuntimeException("Access group does not exist"));

        // delete access group
        groupToDelete.setDeleted(true);
        accessGroupRepository.save(groupToDelete);

        // remove access group id from persons
        List<Person> personList = personRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(accessGroupId);
        personList.forEach(person -> person.setAccessGroup(null));
        personRepository.saveAll(personList);

        // remove n to n
        List<AccessGroupEntranceNtoN> accessGroupEntrance = accessGroupEntranceNtoNRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(accessGroupId);
        accessGroupEntrance.forEach(e -> e.setDeleted(true));
        accessGroupEntranceNtoNRepository.saveAll(accessGroupEntrance);

        // remove schedules
        List<AccessGroupSchedule> schedules = accessGroupScheduleRepository.findAllByGroupToEntranceIdInAndDeletedFalse(
                accessGroupEntrance.stream()
                        .map(AccessGroupEntranceNtoN::getGroupToEntranceId)
                        .collect(Collectors.toList())
        );
        schedules.forEach(schedule -> schedule.setDeleted(true));
        accessGroupScheduleRepository.saveAll(schedules);
    }
}
