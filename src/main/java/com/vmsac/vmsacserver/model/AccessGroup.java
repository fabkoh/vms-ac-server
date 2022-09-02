package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vmsac.vmsacserver.model.EventDto.EventAccessGroupDto;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
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

    @Column(name = "isactive")
    private Boolean isActive;

//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "personId")
    @JsonIgnore
    @OneToMany(mappedBy = "accessGroup")
    private List<Person> persons;

    //@JsonIgnore
    //@OneToMany(mappedBy = "accessGroupEntrance")
    //private List<AccessGroupEntranceNtoN> accessGroupEntrance;

    public AccessGroupDto toDto(){
        if (this.persons == null) {
            return new AccessGroupDto(this.accessGroupId, this.accessGroupName,
                    this.accessGroupDesc, this.isActive, null);
        }
        return new AccessGroupDto(this.accessGroupId, this.accessGroupName,
                this.accessGroupDesc, this.isActive, this.persons.stream().map(Person::accDto).collect(Collectors.toList()));
    }
    public AccessGroupOnlyDto toAccessGroupOnlyDto(){
        return new AccessGroupOnlyDto(this.accessGroupId,this.accessGroupName,this.accessGroupDesc, this.isActive);
    }

    @Override
    public String toString() {
        return "AccessGroup{" +
                "accessGroupId=" + accessGroupId +
                ", accessGroupName='" + accessGroupName + '\'' +
                ", accessGroupDesc='" + accessGroupDesc + '\'' +
                ", deleted=" + deleted +
                ", persons=" + persons +
                '}';
    }

    public EventAccessGroupDto toEventDto(){
        return new EventAccessGroupDto(this.accessGroupId,this.accessGroupName,this.deleted);
    }
}
