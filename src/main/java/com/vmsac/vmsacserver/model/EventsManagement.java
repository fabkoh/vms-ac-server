package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vmsac.vmsacserver.model.EventDto.EventControllerDto;
import com.vmsac.vmsacserver.model.EventDto.EventEntranceDto;
import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="eventsmanagement")
@Builder
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
@SQLDelete(sql = "update eventsmanagement set deleted=true where eventsmanagementid=?")
@Where(clause = "deleted=false")
public class EventsManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventsmanagementid", columnDefinition = "serial")
    private Long eventsManagementId;

    @Column(name = "eventsmanagementname")
    @NotNull
    @NotBlank
    private String eventsManagementName;

    @Column(name = "deleted")
    @JsonIgnore
    private Boolean deleted = false;

    @Type( type = "list-array" )
    @Column(name = "inputeventsid")
    @NotNull
    @NotEmpty
    private List<Long> inputEventsId;

    @Type( type = "list-array" )
    @Column(name = "outputactionsid")
    @NotNull
    @NotEmpty
    private List<Long> outputActionsId;

    @Type( type = "list-array" )
    @Column(name = "triggerschedulesid")
    private List<Long> triggerSchedulesid;


    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "controllerid")
    @JsonIgnore
    private Controller controller;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "entranceid")
    @JsonIgnore
    private Entrance entrance;

    @JsonIgnore
    @OneToMany(mappedBy = "eventsManagement", cascade = CascadeType.ALL)
    private List<EventsManagementNotification> eventsManagementNotifications;



    public EventsManagementDto toDto(List<InputEvent> inputevents,
                                     List<OutputEvent> outputevents,
                                     List<TriggerSchedules> triggerschedules,
                                     EventEntranceDto entranceDto,
                                     EventControllerDto controllerDto){
        return new EventsManagementDto(
                this.eventsManagementId,
                this.eventsManagementName,
                inputevents,
                outputevents,
                triggerschedules,
                entranceDto,
                controllerDto,
                this.eventsManagementNotifications);
    }
}
