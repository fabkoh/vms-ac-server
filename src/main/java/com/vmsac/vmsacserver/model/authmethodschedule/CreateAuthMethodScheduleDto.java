package com.vmsac.vmsacserver.model.authmethodschedule;

import com.vmsac.vmsacserver.model.AuthDevice;
import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import com.vmsac.vmsacserver.model.authmethod.AuthMethodDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateAuthMethodScheduleDto {

    @NotBlank(message = "authMethodScheduleName must not be blank")
    private String authMethodScheduleName;

    @NotBlank(message = "rrule must not be blank")
    private String rrule;

    private String[] rruleArray;

    @NotBlank(message = "timeStart must not be blank")
    private String timeStart;

    @NotBlank(message = "timeEnd must not be blank")
    private String timeEnd;

    @NotNull(message = "authMethod must not be blank")
    private AuthMethod authMethod;

    @NotNull(message = "authDevice must not be blank")
    private AuthDevice authDevice;

    public AuthMethodSchedule toAuthMethodSchedule(){
        return new AuthMethodSchedule(null,authMethodScheduleName,rrule,timeStart,timeEnd,true, false,authDevice,authMethod);
    }
}
