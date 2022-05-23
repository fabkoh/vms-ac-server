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
@Data
public class AuthMethodDto {

    private Long authMethodId;

    private String authMethodDesc;

    private String authMethodCondition;

}
