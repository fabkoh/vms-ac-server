package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.AccessGroup;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface AccessGroupRepository extends JpaRepository<AccessGroup, Long> {

    List<AccessGroup> findByDeleted(Boolean deleted);

    Optional<AccessGroup> findByAccessGroupNameAndDeleted(String accessGroupName, Boolean deleted);

    Optional<AccessGroup> findByAccessGroupIdAndDeleted(Long accessGroupId, Boolean deleted);




}
