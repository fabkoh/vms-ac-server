package com.vmsac.vmsacserver.model.credentialtype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CredentialTypeDto {

    private Long credTypeId;

    private String credTypeName;

    private String credTypeDesc;

}
