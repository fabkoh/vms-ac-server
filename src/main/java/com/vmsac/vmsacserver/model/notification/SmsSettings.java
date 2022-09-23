package com.vmsac.vmsacserver.model.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "smssettings")
@Builder
public class SmsSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "smssettingsid", columnDefinition = "serial")
    private Long smsSettingsId;

    @Column(name = "smsapi")
    private String smsAPI;

    @Column(name = "enabled")
    private Boolean enabled;

}

