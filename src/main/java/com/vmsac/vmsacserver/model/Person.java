package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="Person")
public class Person {

    @Id
    @GeneratedValue
    @Column(name="personid")
    private Long personId;
    @Column(name="firstname")
    private String firstName;
    @Column(name="lastname")
    private String lastName;
    @Column(name="lastfourdigitsofid")
    private String lastFourDigitsOfId;
    @Column(name="mobilenumber")
    private String mobileNumber;
    @Column(name="emailadd")
    private String emailAdd;
}
