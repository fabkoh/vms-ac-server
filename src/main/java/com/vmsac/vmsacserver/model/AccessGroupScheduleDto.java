package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccessGroupScheduleDto {

    @NotNull(message = "accessGroupScheduleId must not blank")
    private Long accessGroupScheduleId;

    @NotNull(message = "accessGroupScheduleName must not be blank")
    private String accessGroupScheduleName;

    @NotNull(message = "rrule must not be blank")
    private String rrule;

    private String timeStart;

    private String timeEnd;

    //link to accgrpentrance NtoN

    public AccessGroupSchedule toAccessGroupSchedule(boolean deleted){
        return new AccessGroupSchedule(accessGroupScheduleId,accessGroupScheduleName,rrule,timeStart,timeEnd,null);
    }

}
