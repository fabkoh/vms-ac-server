package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @JsonIgnore
    @OneToMany(mappedBy = "accessGroup")
//    private List<Person> persons = new ArrayList<>();
    private List<Person> persons;

    public AccessGroupDto toDto(){
        return new AccessGroupDto(this.accessGroupId, this.accessGroupName,
                this.accessGroupDesc,this.persons);
    }
}
