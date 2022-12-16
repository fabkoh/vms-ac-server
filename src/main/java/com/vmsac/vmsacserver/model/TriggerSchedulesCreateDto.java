package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggerSchedulesCreateDto {

    @NotNull
    @NotBlank
    private String triggerName;

    @NotNull
    @NotBlank
    private String rrule;

    @NotNull
    @NotBlank
    private String timeStart;

    @NotNull
    @NotBlank
    private String timeEnd;

    @NotNull
    @Positive
    private Long eventsManagementId;

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
