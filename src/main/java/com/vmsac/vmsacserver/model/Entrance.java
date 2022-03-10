package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "entrances")
public class Entrance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entranceid", columnDefinition = "serial")
    private Long entranceId;

    @Column(name = "entrancename")
    private String entranceName;

    @Column(name = "entrancedesc")
    private String entranceDesc;

    @Column(name = "isactive")
    private Boolean isActive;

    @Column(name = "deleted")
    private Boolean deleted;

    //@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "accessGroupId")
    //@JsonIgnore
   // @OneToMany(mappedBy = "accessGroup")
    //private List<AccessGroup> accessGroups;

    public EntranceDto toDto(){
        /*if (this.accessGroups == null) {
            return new EntranceDto(this.entranceId, this.entranceName,
                    this.entranceDesc, this.isActive, null);
        } */
        return new EntranceDto(this.entranceId, this.entranceName,
                this.entranceDesc, this.isActive,this.accessGroups.stream().map(AccessGroup::toAccessGroupOnlyDto).collect(Collectors.toList()));
    }
    public EntranceOnlyDto toEntranceOnlyDto(){
        return new EntranceOnlyDto(this.entranceId,this.entranceName,this.entranceDesc, this.isActive);
    }
}
