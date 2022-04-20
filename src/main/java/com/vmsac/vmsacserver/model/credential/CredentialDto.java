package com.vmsac.vmsacserver.model.credential;

import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.PersonDto;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import com.vmsac.vmsacserver.model.credentialtype.CredentialTypeDto;
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

    private CredentialTypeDto credType;

    private PersonDto person;
}
