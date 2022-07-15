package com.vmsac.vmsacserver.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmsac.vmsacserver.model.EventDto.EventAccessGroupDto;
import com.vmsac.vmsacserver.model.EventDto.EventControllerDto;
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
@Table(name="events")
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventid", columnDefinition = "serial")
    private Long eventId;

    @Column(name="direction")
    private String direction;

    @Column(name="eventtime")
    private String eventTime;

    @Column(name = "deleted")
    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name="personid")
    private Person person;

    @ManyToOne
    @JoinColumn(name="entranceid")
    private Entrance entrance;

    @ManyToOne
    @JoinColumn(name="accessgroupid")
    private AccessGroup accessGroup;

    @ManyToOne
    @JoinColumn(name="eventactiontypeid")
    private EventActionType eventActionType;

    @ManyToOne
    @JoinColumn(name="controllerid")
    private Controller controller;

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", direction='" + direction + '\'' +
                ", eventTime=" + eventTime +
                ", deleted=" + deleted +
                ", person=" + person +
                ", entrance=" + entrance +
                ", accessGroup=" + accessGroup +
                ", eventActionType=" + eventActionType +
                ", controller=" + controller +
                '}';
    }

    public EventPersonDto getPerson() {
        try{
            return person.toEventDto();}
        catch ( Exception e){
            return null;
        }
    }

    public EventEntranceDto getEntrance() {
        try{
            return entrance.toEventDto();}
        catch ( Exception e){
            return null;
        }

    }

    public EventAccessGroupDto getAccessGroup() {
        try{
            return accessGroup.toEventDto();}
        catch ( Exception e){
            return null;
        }

    }

    public EventControllerDto getController() {
        try{
            return controller.toEventDto();}
        catch ( Exception e){
            return null;
        }

    }
}