package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UniconControllerDto {

    private Long controllerId;

    @NonNull
    private String controllerIP;

    @NonNull
    private Boolean controllerIPStatic;

    @NonNull
    private String controllerMAC;

    @NonNull
    private String controllerSerialNo;

    public Controller toCreateController(String controllerName, LocalDateTime lastOnline, LocalDateTime lastSync, Boolean mastercontroller, LocalDateTime created, String pinAssignmentConfig, String settingsConfig, Boolean deleted){
        return new Controller(null,controllerIPStatic,controllerName,controllerIP,null,
                controllerMAC,controllerSerialNo,lastOnline, lastSync, created,mastercontroller,pinAssignmentConfig,
                settingsConfig,deleted,null, null);
    }

    public Controller toController(){
        return new Controller(controllerId,controllerIPStatic,null,controllerIP,null,
                controllerMAC,controllerSerialNo,null, null, null,null,
                null,null,false,null, null);
    }
//    include auth device to update status ( online/ offline )

}
