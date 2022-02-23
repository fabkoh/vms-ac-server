package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonOnlyDto {

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

    public Person toPerson(boolean deleted,AccessGroup accessGroup){
        return new Person(personId,personFirstName,personLastName,personUid,personMobileNumber,personEmail,deleted,accessGroup );
    }
}
