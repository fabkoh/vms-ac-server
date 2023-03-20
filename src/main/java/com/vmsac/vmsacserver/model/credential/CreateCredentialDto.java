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
public class CreateCredentialDto {

    private Long credId;
    private String credUid;

    private LocalDateTime credTTL;

    private Boolean isValid;

    private Boolean isPerm;

    private Long credTypeId;

    private Long personId;

    public Long getCredId() {
        return credId;
    }

    public void setCredId(Long credId) {
        this.credId = credId;
    }

    public String getCredUid() {
        return credUid;
    }

    public void setCredUid(String credUid) {
        this.credUid = credUid;
    }

    public LocalDateTime getCredTTL() {
        return credTTL;
    }

    public void setCredTTL(LocalDateTime credTTL) {
        this.credTTL = credTTL;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public Boolean getPerm() {
        return isPerm;
    }

    public void setPerm(Boolean perm) {
        isPerm = perm;
    }

    public Long getCredTypeId() {
        return credTypeId;
    }

    public void setCredTypeId(Long credTypeId) {
        this.credTypeId = credTypeId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Credential toCredential() {
        return new Credential(null, credUid, credTTL, isValid, isPerm, null, null, false);
    }
}
