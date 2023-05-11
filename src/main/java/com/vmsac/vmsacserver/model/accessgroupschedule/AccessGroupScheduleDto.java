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
public class AccessGroupScheduleDto {

    @NotNull(message = "accessGroupScheduleId must not blank")
    private Long accessGroupScheduleId;

    @NotBlank(message = "accessGroupScheduleName must not be blank")
    private String accessGroupScheduleName;

    @NotBlank(message = "rrule must not be blank")
    private String rrule;

    private String timeStart;

    private String timeEnd;

    private Long groupToEntranceId;

    private Boolean isActive;

    public AccessGroupSchedule toAccessGroupSchedule(boolean deleted){
        return new AccessGroupSchedule(accessGroupScheduleId, accessGroupScheduleName, rrule, timeStart, timeEnd, groupToEntranceId, isActive, deleted);
    }

}
