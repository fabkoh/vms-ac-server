package com.vmsac.vmsacserver.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="persons")
@Builder
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




    public PersonDto toDto() {
        if(accessGroup == null){
            return new PersonDto(this.personId, this.personFirstName,
                    this.personLastName, this.personUid, this.personMobileNumber,
                    this.personEmail,null);
        }
        return new PersonDto(this.personId, this.personFirstName,
                this.personLastName, this.personUid, this.personMobileNumber,
                this.personEmail,this.accessGroup.toAccessGroupOnlyDto());
    }
    public PersonOnlyDto accDto(){
        return new PersonOnlyDto(this.personId,this.personFirstName,this.personLastName,this.personUid,
        this.personMobileNumber,this.personEmail);
    }
}
