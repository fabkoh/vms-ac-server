package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.AuthDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthDeviceRepository extends JpaRepository<AuthDevice,Long> {

    void deleteByController_ControllerIdEquals(Long controllerId);

    List<AuthDevice> findByEntrance_EntranceIdEquals(Long entranceId);

    List<AuthDevice> findByController_ControllerIdEquals(Long controllerId);

    AuthDevice findByEntrance_EntranceIdIsAndAuthDeviceDirectionContains(Long entranceId, String authDeviceDirection);

    Optional<AuthDevice> findByAuthDeviceId(Long authDeviceId);



}
