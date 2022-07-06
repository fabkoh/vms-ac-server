package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="inputevent")
@Builder
public class InputEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inputeventid")
    private Long inputEventId;

    @Column(name = "timerduration")
    private int timerDuration;

    @ManyToOne
    @JoinColumn(name = "eventsmanagementid")
    private EventsManagement eventsManagement;

    @ManyToOne
    @JoinColumn(name = "eventactioninputid")
    private EventActionInputType eventActionInputType;
}
