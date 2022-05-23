package com.vmsac.vmsacserver.model.authmethodschedule;

import com.vmsac.vmsacserver.model.authmethod.AuthMethodDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthMethodScheduleDto {

    private Long authMethodScheduleId;

    private String authMethodScheduleName;

    private String rrule;

    private String timeStart;

    private String timeEnd;

    private AuthMethodDto authMethod;
}
