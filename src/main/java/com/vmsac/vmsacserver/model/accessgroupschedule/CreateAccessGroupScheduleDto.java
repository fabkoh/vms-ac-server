package com.vmsac.vmsacserver.model.accessgroupschedule;

import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateAccessGroupScheduleDto {

    @NotBlank(message = "accessGroupScheduleName must not be blank")
    private String accessGroupScheduleName;

    @NotBlank(message = "rrule must not be blank")
    private String rrule;

    private String timeStart;

    private String timeEnd;

    private Long groupToEntranceId;

    public AccessGroupSchedule toAccessGroupSchedule(boolean deleted){
        return new AccessGroupSchedule(null, accessGroupScheduleName, rrule, timeStart, timeEnd, groupToEntranceId, deleted);
    }
}
