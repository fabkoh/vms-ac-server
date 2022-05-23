package com.vmsac.vmsacserver.model.authmethodschedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmsac.vmsacserver.model.AuthDevice;
import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import com.vmsac.vmsacserver.model.authmethod.AuthMethodDto;
import com.vmsac.vmsacserver.model.authmethodcredentialtypenton.AuthMethodCredentialTypeNtoN;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "authmethodschedule")
@Builder
public class AuthMethodSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authmethodscheduleid", columnDefinition = "serial")
    private Long authMethodScheduleId;

    @Column(name = "authmethodschedulename")
    private String authMethodScheduleName;

    @Column(name = "rrule")
    private String rrule;

    @Column(name = "timestart")
    private String timeStart;

    @Column(name = "timeend")
    private String timeEnd;

    @Column(name="deleted")
    private Boolean deleted;


    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "authdeviceid",referencedColumnName = "authdeviceid")
    private AuthDevice authDevice;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "authmethodid",referencedColumnName = "authmethodid")
    private AuthMethod authMethod;


    public AuthMethodScheduleDto toDto(){
        return new AuthMethodScheduleDto(authMethodScheduleId,authMethodScheduleName,rrule,timeStart,timeEnd,authMethod.toDto());
    }
}
