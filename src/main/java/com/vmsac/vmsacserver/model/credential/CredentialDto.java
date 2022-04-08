package com.vmsac.vmsacserver.model.credential;

import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CredentialDto {

    private Long credId;

    private String credUid;

    private LocalDateTime credTTL;

    private Boolean isValid;

    private Boolean isPerm;

    private CredentialType credType;

    private Person person;
}
