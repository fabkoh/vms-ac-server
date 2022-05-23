package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthMethodScheduleRepository extends JpaRepository<AuthMethodSchedule,Long> {

    Optional<AuthMethodSchedule> findByAuthMethodScheduleIdAndDeletedFalse(Long authMethodScheduleId);

    List<AuthMethodSchedule> findByAuthDevice_AuthDeviceId(Long authDeviceId);


}
