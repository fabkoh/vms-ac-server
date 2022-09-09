package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Entrance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EntranceRepository extends JpaRepository<Entrance, Long> {

    Boolean existsByEntranceId(Long entranceId);

    List<Entrance> findByDeleted(Boolean deleted);
    Optional<Entrance> findByEntranceNameAndDeletedFalse(String entranceName);

    Optional<Entrance> findByEntranceIdAndDeletedFalse(Long entranceId);

    List<Entrance> findByEntranceIdInAndDeletedFalse(Set<Long> entranceIds);

    List<Entrance> findByEntranceIdInAndDeletedFalseAndIsActiveTrue(Set<Long> entranceIds);

    List<Entrance> findByEntranceIdInAndDeletedFalse(List<Long> entranceIds);

    List<Entrance> findByUsedIsFalseAndDeletedIsFalseOrderByEntranceNameAsc();

    List<Entrance> findByDeletedIsFalse();


}
