package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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

    @Column(name = "repeatToggle")
    private Boolean repeatToggle;

    @Column(name = "interval")
    private int interval;

    @Column(name = "byweekday")
    private int byweekday;

    @Column(name = "bymonthday")
    private int bymonthday;

    @Column(name = "bysetpos")
    private int bysetpos;

    @Column(name = "bymonth")
    private int bymonth;

    @Column(name = "allDay")
    private Boolean allDay;

    @Column(name = "endOfDay")
    private Boolean endOfDay;
}
