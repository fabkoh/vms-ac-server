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
import java.util.List;

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
    private String timeStart;

    @Column(name = "timeend")
    @NotNull
    private String timeEnd;

    @Column(name = "deleted")
    @JsonIgnore
    private Boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "eventsmanagementid")
    @JsonIgnore
    private EventsManagement eventsManagement;

    // added to store rrule object
    @Column(name = "dstart")
    @NotNull
    private String dtstart;

    @Column(name = "until")
    private String until;

    @Column(name = "count")
    private int count;

    @Column(name = "repeattoggle")
    private Boolean repeatToggle;

    @Column(name = "rruleinterval")
    private int rruleinterval;

    @ElementCollection(targetClass=Integer.class)
    @Column(name = "byweekday")
    private List<Integer> byweekday;

    @ElementCollection(targetClass=Integer.class)
    @Column(name = "bymonthday")
    private List<Integer> bymonthday;

    @ElementCollection(targetClass=Integer.class)
    @Column(name = "bysetpos")
    private List<Integer> bysetpos;

    @ElementCollection(targetClass=Integer.class)
    @Column(name = "bymonth")
    private List<Integer> bymonth;

    @Column(name = "allday")
    private Boolean allDay;

    @Column(name = "endofday")
    private Boolean endOfDay;

}
