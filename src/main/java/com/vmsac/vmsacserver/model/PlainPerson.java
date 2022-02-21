package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="persons")
public class PlainPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personid", columnDefinition = "serial")
    private Long personId;

    @Column(name="personfirstname")
    private String personFirstName;

    @Column(name="personlastname")
    private String personLastName;

    @Column(name="personuid")
    private String personUid;

    @Column(name="personmobilenumber")
    private String personMobileNumber;

    @Column(name="personemail")
    private String personEmail;

    @Column(name="deleted")
    private Boolean deleted;

    @Column(name="accessgroupid")
    private Long accessGroupId;

}
