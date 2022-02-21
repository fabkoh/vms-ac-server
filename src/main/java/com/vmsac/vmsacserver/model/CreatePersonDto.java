package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

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


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "accessgroupid")
    private AccessGroup accessGroup;
//    @ManyToOne(cascade = {CascadeType.ALL, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH})
//    @JoinColumn(name = "access_group_accessgroupid")
//    private AccessGroup accessGroup;

//    public AccessGroup getAccessGroup() {
//        return accessGroup;
//    }
//
//    public void setAccessGroup(AccessGroup accessGroup) {
//        this.accessGroup = accessGroup;
//    }

    public Person toPerson(Boolean deleted) {
        return new Person(null, personFirstName, personLastName, personUid,
                personMobileNumber, personEmail, deleted,accessGroup);
    }
}
