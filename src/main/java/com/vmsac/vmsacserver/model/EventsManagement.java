package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="eventsmanagement")
@Builder
public class EventsManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventsmanagementid")
    private Long eventsManagementId;

    @Column(name = "triggername")
    private String triggerName;

    @Column(name = "deleted")
    private Boolean deleted;

    @ElementCollection
    @Column(name = "inputeventsid")
    private List<Long> inputEventsId;

    @ElementCollection
    @Column(name = "outputeventsid")
    private List<Long> outputEventsId;

    @ManyToOne
    @JoinColumn(name = "controllerid")
    private Controller controller;

    @ManyToOne
    @JoinColumn(name = "entranceid")
    private Entrance entrance;

    @OneToMany(mappedBy = "eventsManagement")
    private List<TriggerSchedules> triggerSchedules;
}
