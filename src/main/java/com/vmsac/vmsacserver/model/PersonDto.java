package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.util.annotation.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {

    @Positive(message = "Person Id must be 1 or greater")
    @NotNull(message = "Person Id must not be empty")
    private Long personId;

    @NotBlank(message = "Person first name must not be blank")
    private String personFirstName;

    @NotBlank(message = "Person last name must not be blank")
    private String personLastName;

    @NotBlank(message = "Person UID must not be blank")
    private String personUid;

    // allow empty string or "+{code} {number}" where code is 1 to 3 digits
//    @Pattern(regexp= "|^\\+[0-9]{1,3} [0-9]+$",
//            message = "Person number is not a valid mobile number")
    private String personMobileNumber;

    @Email(message = "Person email is not a valid email")
    private String personEmail;

 
    private AccessGroupOnlyDto accessGroup;
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
        if(accessGroup == null){
            return new Person(personId, personFirstName, personLastName, personUid,
                    personMobileNumber, personEmail, deleted,null);
        }
        return new Person(personId, personFirstName, personLastName, personUid,
                personMobileNumber, personEmail, deleted,accessGroup.toAccessGroup(deleted));
    }
}
