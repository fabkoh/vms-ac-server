package com.vmsac.vmsacserver.model.authmethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmsac.vmsacserver.model.AuthDevice;
import com.vmsac.vmsacserver.model.authmethodcredentialtypenton.AuthMethodCredentialTypeNtoN;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
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
@Table(name = "authmethod")
@Builder
public class AuthMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authmethodid", columnDefinition = "serial")
    private Long authMethodId;

    @Column(name = "authmethoddesc")
    private String authMethodDesc;

    @Column(name = "authmethodcondition")
    private String authMethodCondition;

    @OneToMany(mappedBy = "authMethodId")
    private List<AuthMethodCredentialTypeNtoN> authMethodCredentialTypeNtoNList;

    @JsonIgnore
<<<<<<< HEAD
    @OneToMany(mappedBy = "authDeviceId")
    private List<AuthDevice> authDevices;

    @JsonIgnore
    @OneToOne(mappedBy = "authMethod")
    private AuthMethodSchedule authMethodSchedule;
=======
    @OneToMany(mappedBy = "authMethod", cascade = CascadeType.ALL)
    private List<AuthMethodSchedule> authMethodSchedule;
>>>>>>> 36536e40e84509d9d118a79164c162616bdc73c0

    @Column(name = "deleted")
    private Boolean deleted;

//    @OneToOne(mappedBy = "authMethod")
//    private AuthMethodSchedule authMethodSchedule;

    public AuthMethodDto toDto(){
        return new AuthMethodDto(authMethodId,authMethodDesc,authMethodCondition);
    }
}
