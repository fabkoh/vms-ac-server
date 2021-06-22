package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="visitor")
public class Visitor {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="visitorid")
    private Long visitorId;
    @Column(name="firstname")
    private boolean firstName;
    @Column(name="lastname")
    private String lastName;
    @Column(name="lastfourdigitsofid")
    private String lastFourDigitsOfId;
    @Column(name="mobilenumber")
    private String mobileNumber;
    @Column(name="emailadd")
    private String emailAdd;
}
