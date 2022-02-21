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
public class Person {

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

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "accessgroupid",referencedColumnName = "accessgroupid")
    private AccessGroup accessGroup;


//
//    public AccessGroup getAccessGroup() {
//        return accessGroup;
//    }
//
//    public void setAccessGroup(AccessGroup accessGroup) {
//        this.accessGroup = accessGroup;
//    }

//    @JsonBackReference
//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "accessGroup", referencedColumnName = "accessGroupId")
//    private AccessGroup accessGroup;

    public PersonDto toDto() {
        return new PersonDto(this.personId, this.personFirstName,
                this.personLastName, this.personUid, this.personMobileNumber,
                this.personEmail,this.accessGroup);
    }
    public PersonOnlyDto accDto(){
        return new PersonOnlyDto(this.personId,this.personFirstName,this.personLastName,this.personUid,
        this.personMobileNumber,this.personEmail);
    }
}
