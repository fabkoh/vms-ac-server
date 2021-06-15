package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="Person")
public class Person {

    @Id
    @GeneratedValue
    @Column(name="personId")
    private Long personId;
    private String firstName;
    private String lastName;
    private String lastFourDigitsOfId;
    private String mobileNumber;
    private String emailAdd;
}
