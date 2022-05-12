package com.vmsac.vmsacserver.model.authmethodcredentialtypenton;

import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "AuthMethodCredentialTypeNtoN")
@Builder
public class AuthMethodCredentialTypeNtoN {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authmethodcredentialsntonid", columnDefinition = "serial")
    private Long authMethodCredentialNtoNId;

    private Long authMethodId; // auth method not needed for now

    @ManyToOne
    @JoinColumn(name = "credtypeid")
    private CredentialType credentialType;

    @Column(name = "deleted")
    private Boolean deleted;
}
