package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.AuthDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthDeviceRepository extends JpaRepository<AuthDevice,Long> {

    void deleteByController_ControllerIdEquals(Long controllerId);

    List<AuthDevice> findByEntrance_EntranceIdEquals(Long entranceId);

}
