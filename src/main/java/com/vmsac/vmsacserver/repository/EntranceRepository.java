package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Entrance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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





    @Query(value = "select * from entrances where upper(entrancename) like upper(concat('%', :name, '%')) " +
            "and deleted = false", nativeQuery = true)
    List<Entrance> searchByEntranceName(String name);

}
