package com.vmsac.vmsacserver.model.credentialtype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "credentialtype")
@Builder
public class CredentialType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credtypeid", columnDefinition = "serial")
    private Long credTypeId;

    @Column(name = "credtypename")
    private String credTypeName;

    @Column(name = "credtypedesc")
    private String credTypeDesc;

    @Column(name = "deleted")
    private Boolean deleted;

    public CredentialTypeDto toDto() {
        return new CredentialTypeDto(credTypeId, credTypeName, credTypeDesc);
    }
}
