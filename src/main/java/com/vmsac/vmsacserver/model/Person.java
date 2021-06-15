package com.vmsac.vmsacserver.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;



@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "Person")
public class Person {

    @Id
    @GeneratedValue
    private Long personId;
    private String firstName;
    private String lastName;
    private String lastFourDigitsOfId;
    private String mobileNumber;
    private String emailAdd;
}
