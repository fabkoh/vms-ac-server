package com.vmsac.vmsacserver.model.credential;

import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "credentials")
@Builder
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credid", columnDefinition = "serial")
    private Long credId;

    @Column(name = "creduid")
    private String credUid;

    @Column(name = "credttl")
    private LocalDateTime credTTL;

    @Column(name = "isvalid")
    private Boolean isValid;

    @Column(name = "isprem")
    private Boolean isPerm;

    @ManyToOne
    @JoinColumn(name = "credtypeid")
    private CredentialType credType;

    @ManyToOne
    @JoinColumn(name = "personid")
    private Person person;

    @Column(name = "deleted")
    private Boolean deleted;

    public CredentialDto toDto() {
        return new CredentialDto(credId, credUid, credTTL, isValid, isPerm, credType, person);
    }
}
