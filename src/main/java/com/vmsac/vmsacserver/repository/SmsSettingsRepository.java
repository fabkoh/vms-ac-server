package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.notification.SmsSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsSettingsRepository extends JpaRepository<SmsSettings, Long> {

}
