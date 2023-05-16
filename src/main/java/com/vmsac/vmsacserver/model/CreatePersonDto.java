package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonDto{

    @NotBlank(message = "Person first name must not be blank")
    private String personFirstName;

    @NotBlank(message = "Person last name must not be blank")
    private String personLastName;

    private String personUid;

    // allow empty string or "+{code} {number}" where code is 1 to 3 digits
    //@Pattern(message = "Person number is not a mobile number")
    private String personMobileNumber;

    @Email(message = "Person email is not an email")
    private String personEmail;

    private AccessGroup accessGroup;

    public String getPersonFirstName() {
        return personFirstName;
    }

    public void setPersonFirstName(String personFirstName) {
        this.personFirstName = personFirstName;
    }

    public String getPersonLastName() {
        return personLastName;
    }

    public void setPersonLastName(String personLastName) {
        this.personLastName = personLastName;
    }

    public String getPersonUid() {
        return personUid;
    }

    public void setPersonUid(String personUid) {
        this.personUid = personUid;
    }

    public String getPersonMobileNumber() {
        return personMobileNumber;
    }

    public void setPersonMobileNumber(String personMobileNumber) {
        this.personMobileNumber = personMobileNumber;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public AccessGroup getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }

    public Person toPerson(Boolean deleted) {
        return new Person(null, personFirstName, personLastName, personUid,
                personMobileNumber, personEmail, deleted,accessGroup);
    }
}
