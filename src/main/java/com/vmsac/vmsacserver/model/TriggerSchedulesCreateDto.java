package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
}
