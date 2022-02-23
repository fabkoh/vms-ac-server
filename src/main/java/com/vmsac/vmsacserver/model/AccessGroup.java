package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "accessgroups")
public class AccessGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accessgroupid", columnDefinition = "serial")
    private Long accessGroupId;

    @Column(name = "accessgroupname")
    private String accessGroupName;

    @Column(name = "accessgroupdesc")
    private String accessGroupDesc;

    @Column(name = "deleted")
    private Boolean deleted;

//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "personId")
    @JsonIgnore
    @OneToMany(mappedBy = "accessGroup")
    private List<Person> persons;

    public AccessGroupDto toDto(){
        if (this.persons == null) {
            return new AccessGroupDto(this.accessGroupId, this.accessGroupName,
                    this.accessGroupDesc,null);
        }
        return new AccessGroupDto(this.accessGroupId, this.accessGroupName,
                this.accessGroupDesc,this.persons.stream().map(Person::accDto).collect(Collectors.toList()));
    }
    public AccessGroupOnlyDto toAccessGroupOnlyDto(){
        return new AccessGroupOnlyDto(this.accessGroupId,this.accessGroupName,this.accessGroupDesc);
    }
}
