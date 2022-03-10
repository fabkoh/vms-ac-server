package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessGroupEntranceNtoNRepository extends JpaRepository<AccessGroupEntranceNtoN, Long> {

    List<AccessGroupEntranceNtoN> findAllByDeletedFalse();

    List<AccessGroupEntranceNtoN> findAllByEntranceEntranceIdAndDeletedFalse(Long entranceId);

    List<AccessGroupEntranceNtoN> findAllByAccessGroupAccessGroupIdAndDeletedFalse(Long accessGroupId);

    List<AccessGroupEntranceNtoN> findAllByAccessGroupAccessGroupIdAndEntranceEntranceIdAndDeletedFalse(Long accessGroupId, Long entranceId);

    List<AccessGroupEntranceNtoN> findAllByEntranceEntranceIdAndAccessGroupAccessGroupIdInAndDeletedFalse(Long entranceId, List<Long> accessGroupIds);
}
