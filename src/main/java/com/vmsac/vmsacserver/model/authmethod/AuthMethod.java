package com.vmsac.vmsacserver.model.authmethod;

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

    @Column(name = "deleted")
    private Boolean deleted;
}
