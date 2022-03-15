package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.Entrance;
import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.PersonOnlyDto;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoNDto;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.EntranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public void assignEntranceToAccessGroups(Long entranceId, List<Long> accessGroupIds) throws Exception {
        Set<Long> accessGroupIdsSet = new HashSet<>(accessGroupIds);

        // first, remove all accessGroupEntrance not in accessGroupIds
        List<AccessGroupEntranceNtoN> accessGroupEntranceListInDB = accessGroupEntranceRepository.findAllByEntranceEntranceIdAndDeletedFalse(entranceId);
        List<AccessGroupEntranceNtoN> changes = new ArrayList<>();
        for (AccessGroupEntranceNtoN accessGroupEntrance : accessGroupEntranceListInDB) {
            Long accessGroupId = accessGroupEntrance.getAccessGroup().getAccessGroupId();
            if (!accessGroupIdsSet.contains(accessGroupId)) { // remove this access group from entrance
                accessGroupEntrance.setDeleted(true);
                changes.add(accessGroupEntrance);
            } else {
                accessGroupIdsSet.remove(accessGroupId); // ids left in access group ids are to be created
            }
        }

        Entrance entrance = entranceRepository.findByEntranceIdAndDeletedFalse(entranceId).orElseThrow(() -> new RuntimeException("Entrance does not exist"));
        List<AccessGroup> accessGroupsToBeAdded = accessGroupRepository.findByAccessGroupIdInAndDeletedFalse(accessGroupIdsSet);
        if(accessGroupsToBeAdded.size() != accessGroupIdsSet.size()) {
            throw new RuntimeException("Access group(s) does not exist");
        }
        accessGroupsToBeAdded.forEach(
                accessGroup -> changes.add(
                        new AccessGroupEntranceNtoN(null, accessGroup, entrance, false)
                )
        );

        accessGroupEntranceRepository.saveAll(changes);
    }

    public void assignAccessGroupToEntrances(Long accessGroupId, List<Long> entranceIds) {
        Set<Long> entranceIdsSet = new HashSet<>(entranceIds);

        // first, remove all accessGroupEntrance not in entranceIds
        List<AccessGroupEntranceNtoN> accessGroupEntranceListInDB = accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(accessGroupId);
        List<AccessGroupEntranceNtoN> changes = new ArrayList<>();
        for(AccessGroupEntranceNtoN accessGroupEntrance : accessGroupEntranceListInDB) {
            Long entranceId = accessGroupEntrance.getEntrance().getEntranceId();
            if (entranceIdsSet.contains(entranceId)) {
                entranceIdsSet.remove(entranceId); // ids left in entrance ids are to be created
            } else { // remove this entrance from access group
                accessGroupEntrance.setDeleted(true);
                changes.add(accessGroupEntrance);
            }
        }

        AccessGroup accessGroup = accessGroupRepository.findByAccessGroupIdAndDeletedFalse(accessGroupId).orElseThrow(() -> new RuntimeException("Access group does not exist"));
        List<Entrance> entrancesToBeAdded = entranceRepository.findBByEntranceIdInAndDeletedFalse(entranceIdsSet);
        if (entrancesToBeAdded.size() != entranceIdsSet.size()) {
            throw new RuntimeException("Entrance(s) does not exist");
        }
        entrancesToBeAdded.forEach(
                entrance -> changes.add(
                        new AccessGroupEntranceNtoN(null, accessGroup, entrance, false)
                )
        );

        accessGroupEntranceRepository.saveAll(changes);
    }
}
