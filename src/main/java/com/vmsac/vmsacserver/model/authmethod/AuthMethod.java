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
@Table(name = "authdevice")
@Builder
public class AuthMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authdeviceid", columnDefinition = "serial")
    private Long authMethodId;

    @Column(name = "authMethodDesc")
    private String authMethodDesc;

    @Column(name = "authMethodCondition")
    private String authMethodCondition;

    @OneToMany(mappedBy = "authMethodId")
    private List<AuthMethodCredentialTypeNtoN> authMethodCredentialTypeNtoNList;

    @Column(name = "deleted")
    private Boolean deleted;
}
