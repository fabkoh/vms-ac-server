package com.vmsac.vmsacserver.repository;

import com.vmsac.vmsacserver.model.notification.EmailSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailSettingsRepository extends JpaRepository<EmailSettings, Long> {

}
