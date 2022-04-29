package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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

//    @OneToMany ( cascade = CascadeType.REMOVE )
//    @JoinColumn(name="authschedule", referencedColumnName = "authscheduleid")
//    private AuthSchedule authSchedule;
    public AuthDevice toCreateAuthDevice(String authDeviceName, String authDeviceDirection,
                                         String defaultAuthMethod, Controller controller) {
        return new AuthDevice(null,authDeviceName,authDeviceDirection,null, false,
                defaultAuthMethod,controller,null);
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
                ", entrance=" + entrance +
                '}';
    }
}
