package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.EventsManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventsManagementRepository extends JpaRepository<EventsManagement, Long> {

    Optional<EventsManagement> findByDeletedFalseAndEventsManagementId(Long eventsManagementId);

    Boolean existsByDeletedFalseAndEventsManagementId(Long eventsManagementId);

    List<EventsManagement> findAllByDeletedFalse();

    List<EventsManagement> findByDeletedFalseAndEntrance_EntranceIdOrderByEventsManagementNameAsc(Long entranceId);

    List<EventsManagement> findByDeletedFalseAndController_ControllerIdOrderByEventsManagementNameAsc(Long controllerId);


}
