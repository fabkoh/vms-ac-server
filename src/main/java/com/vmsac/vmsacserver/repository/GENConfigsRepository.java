package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.GENConfigs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GENConfigsRepository extends JpaRepository<GENConfigs, Long> {
    GENConfigs getByController_ControllerIdAndPinName(Long controllerId, String pinName);

    List<GENConfigs> findByController_ControllerId(Long controllerId);
}