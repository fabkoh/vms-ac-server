package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "authdevice")
@Builder
public class AuthDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column ( name = "authdeviceid",columnDefinition = "serial")
    private Long authDeviceId;

    @Column(name="authdevicename")
    private String authDeviceName;

    @Column(name="authdevicedirection")
    private String authDeviceDirection;

    @Column(name="lastonline")
    private LocalDateTime lastOnline;

    @Column(name="masterpin")
    private Boolean masterpin;

    @Column(name="defaultauthmethod")
    private String defaultAuthMethod;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="controllerid")
    private Controller controller;

    @ManyToOne
    @JoinColumn(name="entranceid")
    private Entrance entrance;

    @JsonIgnore
    @OneToMany(mappedBy = "authDevice", cascade = CascadeType.MERGE)
    private List<AuthMethodSchedule> authMethodSchedules = new ArrayList<>();

    public AuthDevice toCreateAuthDevice(String authDeviceName, String authDeviceDirection,
                                         String defaultAuthMethod, Controller controller) {
        return new AuthDevice(null,authDeviceName,authDeviceDirection,null, false,
                defaultAuthMethod,controller,null,null);
    }

    @Override
    public String toString() {
        return "AuthDevice{" +
                "authDeviceId=" + authDeviceId +
                ", authDeviceName='" + authDeviceName + '\'' +
                ", authDeviceDirection='" + authDeviceDirection + '\'' +
                ", lastOnline=" + lastOnline +
                ", masterpin=" + masterpin +
                ", defaultAuthMethod='" + defaultAuthMethod + '\'' +
                '}';
    }
}
