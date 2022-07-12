package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="triggerschedules")
@Builder
@SQLDelete(sql = "update triggerschedules set deleted=true where triggerscheduleid=?")
@Where(clause = "deleted = false")
public class TriggerSchedules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "triggerscheduleid", columnDefinition = "serial")
    private Long triggerScheduleId;

    @Column(name = "triggername")
    @NotNull
    @NotBlank
    private String triggerName;

    @Column(name = "rrule")
    @NotNull
    @NotBlank
    private String rrule;

    @Column(name = "timestart")
    @NotNull
    private LocalTime timeStart;

    @Column(name = "timeend")
    @NotNull
    private LocalTime timeEnd;

    @Column(name = "deleted")
    @JsonIgnore
    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name = "eventsmanagementid")
    @JsonIgnore
    private EventsManagement eventsManagement;
}
