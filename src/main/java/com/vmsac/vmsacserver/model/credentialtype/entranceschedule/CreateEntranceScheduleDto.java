package com.vmsac.vmsacserver.model.credentialtype.entranceschedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateEntranceScheduleDto {

    @NotBlank(message = "entranceScheduleName must not be blank")
    private String entranceScheduleName;

    @NotBlank(message = "rrule must not be blank")
    private String rrule;

    @NotBlank(message = "timeStart must not be blank")
    private String timeStart;

    @NotBlank(message = "timeEnd must not be blank")
    private String timeEnd;

    private Long entranceId;

    public EntranceSchedule toEntranceSchedule(boolean deleted){
        return new EntranceSchedule(null, entranceScheduleName, rrule, timeStart, timeEnd, entranceId, deleted, true);
    }
}
