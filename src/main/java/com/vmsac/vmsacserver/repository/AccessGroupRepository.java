package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.AccessGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AccessGroupRepository extends JpaRepository<AccessGroup, Long> {

    List<AccessGroup> findByDeleted(Boolean deleted);

    Optional<AccessGroup> findByAccessGroupNameAndDeleted(String accessGroupName, Boolean deleted);

    Optional<AccessGroup> findByAccessGroupIdAndDeleted(Long accessGroupId, Boolean deleted);

    List<AccessGroup> findByAccessGroupIdInAndDeletedFalse(Set<Long> accessGroupIds);

    Optional<AccessGroup> findByAccessGroupIdAndDeletedFalse(Long accessGroupId);

    @Query(value = "select * from accessgroups where upper(accessgroupname) like upper(concat('%', :name, '%')) " +
            "and deleted = false", nativeQuery = true)
    List<AccessGroup> searchByAccessGroupName(String name);

}
