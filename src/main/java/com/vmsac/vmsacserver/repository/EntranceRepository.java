package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Entrance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntranceRepository extends JpaRepository<Entrance, Long> {

    List<Entrance> findByDeleted(Boolean deleted);

    Optional<Entrance> findByEntranceNameAndDeleted(String entranceName, Boolean deleted);

    Optional<Entrance> findByEntranceIdAndDeleted(Long entranceId, Boolean deleted);
}
