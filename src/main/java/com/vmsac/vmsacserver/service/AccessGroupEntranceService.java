package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoNDto;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccessGroupEntranceService {
// all methods assume deleted == false
    @Autowired
    AccessGroupEntranceNtoNRepository accessGroupEntranceRepository;

    public List<AccessGroupEntranceNtoNDto> findAll() {
        return accessGroupEntranceRepository.findAllByDeleted(false)
                .stream()
                .map(AccessGroupEntranceNtoN::toDto)
                .collect(Collectors.toList());
    }

    public List<AccessGroupEntranceNtoNDto> findAllWhereEntranceId(Long entranceId) {
        return accessGroupEntranceRepository.findAllByEntranceEntranceIdAndDeleted(entranceId, false)
                .stream()
                .map(AccessGroupEntranceNtoN::toDto)
                .collect(Collectors.toList());
    }

    public List<AccessGroupEntranceNtoNDto> findAllWhereAccessGroupId(Long accessGroupId) {
        return accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndDeleted(accessGroupId, false)
                .stream()
                .map(AccessGroupEntranceNtoN::toDto)
                .collect(Collectors.toList());
    }

    public List<AccessGroupEntranceNtoNDto> findAllWhereAccessGroupIdAndEntranceId(Long accessGroupId, Long entranceId) {
        return accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndEntranceEntranceIdAndDeleted(accessGroupId, entranceId, false)
                .stream()
                .map(AccessGroupEntranceNtoN::toDto)
                .collect(Collectors.toList());
    }

    public void assignEntranceToAccessGroups(Long entranceId, List<Long> accessGroupIds) {
        List<AccessGroupEntranceNtoN> listInDB = accessGroupEntranceRepository.findAllByEntranceEntranceIdAndAccessGroupAccessGroupIdInAndDeleted(entranceId, accessGroupIds, false);

    }
}
