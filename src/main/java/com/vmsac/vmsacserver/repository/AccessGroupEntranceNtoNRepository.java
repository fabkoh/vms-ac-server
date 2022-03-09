package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessGroupEntranceNtoNRepository extends JpaRepository<AccessGroupEntranceNtoN, Long> {

    List<AccessGroupEntranceNtoN> findAllByDeleted(Boolean deleted);

    List<AccessGroupEntranceNtoN> findAllByEntranceEntranceIdAndDeleted(Long entranceId, Boolean deleted);

    List<AccessGroupEntranceNtoN> findAllByAccessGroupAccessGroupIdAndDeleted(Long accessGroupId, Boolean deleted);

    List<AccessGroupEntranceNtoN> findAllByAccessGroupAccessGroupIdAndEntranceEntranceIdAndDeleted(Long accessGroupId, Long entranceId, Boolean deleted);

    List<AccessGroupEntranceNtoN> findAllByEntranceEntranceIdAndAccessGroupAccessGroupIdInAndDeleted(Long entranceId, List<Long> accessGroupIds, Boolean deleted);
}
