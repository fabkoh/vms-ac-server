package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthMethodScheduleRepository extends JpaRepository<AuthMethodSchedule,Long> {

    List<AuthMethodSchedule> findByAuthDevice_AuthDeviceIdAndDeletedFalse(Long authDeviceId);

    Optional<AuthMethodSchedule> findByAuthMethodScheduleIdAndDeletedFalse(Long authMethodScheduleId);

    List<AuthMethodSchedule> findByAuthDevice_Controller_ControllerId(Long controllerId);

    List<AuthMethodSchedule> findByIsActiveTrueAndDeletedFalseAndAuthDevice_AuthDeviceId(Long authDeviceId);




}
