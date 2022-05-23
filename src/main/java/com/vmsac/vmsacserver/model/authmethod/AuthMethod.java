package com.vmsac.vmsacserver.model.authmethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @OneToOne(mappedBy = "authMethod")
    private AuthMethodSchedule authMethodSchedule;

    @Column(name = "deleted")
    private Boolean deleted;

//    @OneToOne(mappedBy = "authMethod")
//    private AuthMethodSchedule authMethodSchedule;

    public AuthMethodDto toDto(){
        return new AuthMethodDto(authMethodId,authMethodDesc,authMethodCondition);
    }
}
