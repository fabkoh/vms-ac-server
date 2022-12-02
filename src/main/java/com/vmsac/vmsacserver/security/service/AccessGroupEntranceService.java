package com.vmsac.vmsacserver.security.service;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.Entrance;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoNDto;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.AccessGroupScheduleRepository;
import com.vmsac.vmsacserver.repository.EntranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccessGroupEntranceService {
// all methods assume deleted == false
    @Autowired
    AccessGroupEntranceNtoNRepository accessGroupEntranceRepository;

    @Autowired
    AccessGroupRepository accessGroupRepository;

    @Autowired
    EntranceRepository entranceRepository;

    @Autowired
    AccessGroupScheduleRepository accessGroupScheduleRepository;

    public List<AccessGroupEntranceNtoNDto> findAll() {
        return accessGroupEntranceRepository.findAllByDeletedFalse()
                .stream()
                .map(AccessGroupEntranceNtoN::toDto)
                .collect(Collectors.toList());
    }

    public List<AccessGroupEntranceNtoNDto> findAllWhereEntranceId(Long entranceId) {
        return accessGroupEntranceRepository.findAllByEntranceEntranceIdAndDeletedFalse(entranceId)
                .stream()
                .map(AccessGroupEntranceNtoN::toDto)
                .collect(Collectors.toList());
    }

    public List<AccessGroupEntranceNtoNDto> findAllWhereAccessGroupId(Long accessGroupId) {
        return accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(accessGroupId)
                .stream()
                .map(AccessGroupEntranceNtoN::toDto)
                .collect(Collectors.toList());
    }

    public List<AccessGroupEntranceNtoNDto> findAllWhereAccessGroupIdAndEntranceId(Long accessGroupId, Long entranceId) {
        return accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndEntranceEntranceIdAndDeletedFalse(accessGroupId, entranceId)
                .stream()
                .map(AccessGroupEntranceNtoN::toDto)
                .collect(Collectors.toList());
    }

    // at end of transaction, only access group ids would be tied to entrance id
    // remove all schedules with deleted groupToEntranceId
    // add default schedules to new groupToEntranceId
    public void assignAccessGroupsToEntrance(List<Long> accessGroupIds, Long entranceId) throws Exception {
        Set<Long> accessGroupIdsSet = new HashSet<>(accessGroupIds);

        // first, find nton to delete
        List<AccessGroupEntranceNtoN> accessGroupEntranceListInDB = accessGroupEntranceRepository.findAllByEntranceEntranceIdAndDeletedFalse(entranceId);
        List<AccessGroupEntranceNtoN> toDelete = accessGroupEntranceListInDB.stream()
                .filter(groupEntrance -> {
                    Long accessGroupId = groupEntrance.getAccessGroup().getAccessGroupId();
                    if (accessGroupIdsSet.contains(accessGroupId)) {
                        accessGroupIdsSet.remove(accessGroupId); // ids left in set are for add
                        return false; // do not keep this group for toDelete
                    }
                    groupEntrance.setDeleted(true);
                    return true; // keep this group for toDelete
                })
                .collect(Collectors.toList());

        // ensure all parameters are valid (all entrance / access group exists)
        Entrance entrance = entranceRepository.findByEntranceIdAndDeletedFalse(entranceId).orElseThrow(() -> new RuntimeException("Entrance does not exist"));
        List<AccessGroup> accessGroupsToBeAdded = accessGroupRepository.findByAccessGroupIdInAndDeletedFalse(accessGroupIdsSet);
        if(accessGroupsToBeAdded.size() != accessGroupIdsSet.size()) {
            throw new RuntimeException("Access group(s) does not exist");
        }

        // delete all relationships and their schedules
        deleteAccessGroupEntranceNtoN(toDelete);

        // add new relationships and add new schedules
        addNewAccessGroupEntranceNtoN(
                accessGroupsToBeAdded.stream()
                        .map(group -> new AccessGroupEntranceNtoN(
                                null,
                                group,
                                entrance,
                                false
                        ))
                        .collect(Collectors.toList())
        );
    }

    public void assignEntrancesToAccessGroup(List<Long> entranceIds, Long accessGroupId) {
        Set<Long> entranceIdsSet = new HashSet<>(entranceIds);

        // first, find nton to delete
        List<AccessGroupEntranceNtoN> accessGroupEntranceListInDB = accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(accessGroupId);
        List<AccessGroupEntranceNtoN> toDelete = accessGroupEntranceListInDB.stream()
                .filter(groupEntrance -> {
                    Long entranceId = groupEntrance.getEntrance().getEntranceId();
                    if(entranceIdsSet.contains(entranceId)) { // keep it (do not add in toDelete)
                        entranceIdsSet.remove(entranceId); // ids left in set are for add
                        return false;
                    }
                    groupEntrance.setDeleted(true);
                    return true; // delete this
                })
                .collect(Collectors.toList());

        // ensure all parameters are valid (all entrance / access group exists)
        AccessGroup accessGroup = accessGroupRepository.findByAccessGroupIdAndDeletedFalse(accessGroupId).orElseThrow(() -> new RuntimeException("Access group does not exist"));
        List<Entrance> entrancesToBeAdded = entranceRepository.findByEntranceIdInAndDeletedFalse(entranceIdsSet);
        if (entrancesToBeAdded.size() != entranceIdsSet.size()) {
            throw new RuntimeException("Entrance(s) does not exist");
        }

        // delete all relationships and their schedules
        deleteAccessGroupEntranceNtoN(toDelete);

        // add new relationships and add default schedules
        addNewAccessGroupEntranceNtoN(
                entrancesToBeAdded.stream()
                        .map(entrance -> new AccessGroupEntranceNtoN(
                                null,
                                accessGroup,
                                entrance,
                                false
                        ))
                        .collect(Collectors.toList())
        );
    }

    // adds all access group entrances
    // get their new ids and adds default schedule for all of them
    // WARNING: does not check if there's already an existing relationship ie can lead to duplicate relationships
    private void addNewAccessGroupEntranceNtoN(List<AccessGroupEntranceNtoN> accessGroupEntrances) {
        List<AccessGroupEntranceNtoN> added = accessGroupEntranceRepository.saveAll(accessGroupEntrances);

        // get default rrule
        String defaultRrule = getDefaultRrule();

        accessGroupScheduleRepository.saveAll( // save default schedule for each relationship
                added.stream()
                        .map(nton -> new AccessGroupSchedule(
                            null,
                            "Default Schedule",
                            defaultRrule,
                            "00:00",
                            "23:59",
                            nton.getGroupToEntranceId(),
                            true,
                            false
                        ))
                        .collect(Collectors.toList())
        );
    }

    // delete all given access group entrances
    // gets their ids and remove their schedules also
    // WARNING: does not check if access group entrances still exists
    //          assumes deleted in given list is true already
    public void deleteAccessGroupEntranceNtoN(List<AccessGroupEntranceNtoN> accessGroupEntrances) {
        accessGroupEntranceRepository.saveAll(accessGroupEntrances);
        List<AccessGroupSchedule> toDelete = accessGroupScheduleRepository.findAllByGroupToEntranceIdInAndDeletedFalse(
                accessGroupEntrances.stream().map(AccessGroupEntranceNtoN::getGroupToEntranceId).collect(Collectors.toList())
        );
        toDelete.forEach(schedule -> schedule.setDeleted(true));
        accessGroupScheduleRepository.saveAll(toDelete);
    }

    // returns the default string (24 / 7)
    // format: "DTSTART:{yyyymmdd}T000000Z\nRRULE:FREQ=DAILY;INTERVAL=1;WKST=MO"
    private String getDefaultRrule() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMdd");
        return "DTSTART:" + dtf.format(LocalDateTime.now()) + "T000000Z\nRRULE:FREQ=DAILY;INTERVAL=1;WKST=MO";
    }
}
