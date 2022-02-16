package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "accessgroups")
public class AccessGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accessgroupid")
    private Long accessGroupId;

    @Column(name = "accessgroupname")
    private String accessGroupName;

    @Column(name = "accessgroupdesc")
    private String accessGroupDesc;

    @Column(name = "deleted")
    private Boolean deleted;

//    @OneToMany(mappedBy = "accessGroup", cascade = {CascadeType.ALL, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH})
//    private List<Person> persons = new ArrayList<>();

    public AccessGroupDto toDto(){
        return new AccessGroupDto(this.accessGroupId, this.accessGroupName,
                this.accessGroupDesc);
    }
}
