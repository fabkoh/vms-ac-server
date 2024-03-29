package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vmsac.vmsacserver.model.EventDto.EventEntranceDto;
import com.vmsac.vmsacserver.model.EventDto.EventPersonDto;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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

    @Column(name = "used")
    private Boolean used;

    @Column(name = "thirdpartyoption")
    private String thirdPartyOption;

    @JsonIgnore
    @OneToMany(mappedBy = "entrance", fetch = FetchType.LAZY)
    private List<AuthDevice> entranceAuthDevices;

    @OneToMany(mappedBy = "entrance", cascade = CascadeType.ALL)
    private List<EventsManagement> eventsManagements;

    public EntranceDto toDto(){
        return new EntranceDto(this.entranceId, this.entranceName, this.entranceDesc,
                this.isActive, this.used, thirdPartyOption, null,this.entranceAuthDevices,
                this.eventsManagements);
    }
    public EntranceOnlyDto toEntranceOnlyDto(){
        return new EntranceOnlyDto(this.entranceId, this.entranceName, this.entranceDesc, this.isActive,
                this.used, this.thirdPartyOption, this.entranceAuthDevices, this.eventsManagements);
    }

    @Override
    public String toString() {
        return "Entrance{" +
                "entranceId=" + entranceId +
                ", entranceName='" + entranceName + '\'' +
                ", entranceDesc='" + entranceDesc + '\'' +
                ", isActive=" + isActive +
                ", deleted=" + deleted +
                ", used=" + used +
                ", entranceAuthDevices=" + entranceAuthDevices +
                '}';
    }

    public EventEntranceDto toEventDto(){
        return new EventEntranceDto(this.entranceId,this.entranceName,this.deleted);
    }

    @JsonIgnore
    public Controller getAssignedController() {
        if (!entranceAuthDevices.isEmpty())
            return entranceAuthDevices.get(0).getController();
        else return null;
    }
}
