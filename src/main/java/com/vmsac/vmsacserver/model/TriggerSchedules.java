package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="triggerschedules")
@Builder
public class TriggerSchedules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "triggerscheduleid")
    private Long triggerScheduleId;

    @Column(name = "triggername")
    private String triggerName;

    @Column(name = "rrule")
    private String rrule;

    @Column(name = "timestart")
    private LocalTime timeStart;

    @Column(name = "timeend")
    private LocalTime timeEnd;

    @Column(name = "deleted")
    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name = "eventsmanagementid")
    private EventsManagement eventsManagement;
}
