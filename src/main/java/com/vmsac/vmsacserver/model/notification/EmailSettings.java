package com.vmsac.vmsacserver.model.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "emailsettings")
@Builder
public class EmailSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emailsettingsid", columnDefinition = "serial")
    private Long emailSettingsId;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "emailpassword")
    private String emailPassword;

    @Column(name = "hostaddress")
    private String hostAddress;

    @Column(name = "portnumber")
    private String portNumber;

    @Column(name = "enabled")
    private Boolean enabled;

}
