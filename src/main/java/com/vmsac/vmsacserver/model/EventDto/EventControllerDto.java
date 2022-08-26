package com.vmsac.vmsacserver.model.EventDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventControllerDto {

    private Long controllerId;

    private String controllerName;

    private Boolean deleted;

    private String controllerSerialNo;
}
