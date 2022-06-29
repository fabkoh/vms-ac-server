package com.vmsac.vmsacserver.model;


import com.vmsac.vmsacserver.model.EventDto.EventAccessGroupDto;
import com.vmsac.vmsacserver.model.EventDto.EventEntranceDto;
import com.vmsac.vmsacserver.model.EventDto.EventPersonDto;
import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="entranceevent")
@Builder
public class EntranceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventid", columnDefinition = "serial")
    private Long entranceEventId;

    @Column(name="direction")
    private String direction;

    @Column(name="eventtime", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "deleted")
    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name="personid")
    private Person person;

    @ManyToOne
    @JoinColumn(name="entranceid", nullable = false)
    private Entrance entrance;

    @ManyToOne
    @JoinColumn(name="accessgroupid")
    private AccessGroup accessGroup;

    @ManyToOne
    @JoinColumn(name="actiontypeid")
    private EntranceEventType entranceEventType;

    @ManyToOne
    @JoinColumn(name="authmethodid")
    private AuthMethod authMethod;

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + entranceEventId +
                ", direction='" + direction + '\'' +
                ", eventTime=" + eventTime +
                ", deleted=" + deleted +
                ", person=" + person +
                ", entrance=" + entrance +
                ", accessGroup=" + accessGroup +
                ", eventActionType=" + entranceEventType +
                '}';
    }

    public EventPersonDto getPersonDto() {
        try{
        return person.toEventDto();}
        catch ( Exception e){
            return null;
        }
    }

    public EventEntranceDto getEntranceDto() {
        try{
            return entrance.toEventDto();}
        catch ( Exception e){
            return null;
        }

    }

    public EventAccessGroupDto getAccessGroupDto() {
        try{
            return accessGroup.toEventDto();}
        catch ( Exception e){
            return null;
        }

    }

}