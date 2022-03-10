package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Entrance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EntranceRepository extends JpaRepository<Entrance, Long> {

    Optional<Entrance> findByEntranceIdAndDeletedFalse(Long entranceId);

    List<Entrance> findBByEntranceIdInAndDeletedFalse(Set<Long> entranceIds);

}
