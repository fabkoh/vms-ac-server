package com.vmsac.vmsacserver.model.credentialtype.entranceschedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EntranceScheduleDto {

    @NotNull(message = "entranceScheduleId must not blank")
    private Long entranceScheduleId;

    @NotBlank(message = "entranceScheduleName must not be blank")
    private String entranceScheduleName;

    @NotBlank(message = "rrule must not be blank")
    private String rrule;

    @NotBlank(message = "timeStart must not be blank")
    private String timeStart;

    @NotBlank(message = "timeEnd must not be blank")
    private String timeEnd;

    private Long entranceId;

    private boolean isActive;

    public EntranceSchedule toEntranceSchedule(boolean deleted){
        return new EntranceSchedule(entranceScheduleId, entranceScheduleName, rrule, timeStart, timeEnd, entranceId, deleted, isActive);
    }
}
