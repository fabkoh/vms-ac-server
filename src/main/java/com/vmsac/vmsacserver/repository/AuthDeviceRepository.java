package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.AuthDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthDeviceRepository extends JpaRepository<AuthDevice,Long> {

    void deleteByController_ControllerIdEquals(Long controllerId);

    List<AuthDevice> findByEntrance_EntranceIdEquals(Long entranceId);

    AuthDevice findByEntrance_EntranceIdIsAndAuthDeviceDirectionContains(Long entranceId, String authDeviceDirection);



}
