package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "entrances")
@Builder
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

    public EntranceDto toDto(){
        return new EntranceDto(this.entranceId, this.entranceName,
                this.entranceDesc, this.isActive, null);
    }
    public EntranceOnlyDto toEntranceOnlyDto(){
        return new EntranceOnlyDto(this.entranceId,this.entranceName,this.entranceDesc, this.isActive);
    }
}
