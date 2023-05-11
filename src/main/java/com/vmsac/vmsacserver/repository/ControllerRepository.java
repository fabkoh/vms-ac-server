package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.Controller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ControllerRepository extends JpaRepository<Controller, Long>{

    List<Controller> findByDeletedIsFalseOrderByCreatedDesc();

    Optional<Controller> findByControllerSerialNoEqualsAndDeletedIsFalse(String controllerSerialNo);

    Optional<Controller> findByControllerIdEquals(Long controllerId);

    Optional<Controller> findByControllerIdEqualsAndDeletedFalse(Long controllerId);

    boolean existsByControllerNameEqualsAndDeletedFalse(String controllerName);

    boolean existsByDeletedFalseAndAndControllerId(Long controllerId);

    Optional<Controller> findByControllerNameAndDeletedFalse(String controllerName);

    @Query(value = "select * from controller where upper(controllername) like upper(concat('%', :name, '%')) " +
            "or controllerserialno like concat('%', :name, '%') and deleted = false", nativeQuery = true)
    List<Controller> searchByControllerName(String name);

    boolean existsByPendingIPAndDeletedFalse(String pendingIP);

    boolean existsByControllerIPAndDeletedFalse(String controllerIP);

    Optional<Controller> findByAuthDevices_Entrance_EntranceIdAndDeletedFalse(Long entranceId);








}
