package com.vmsac.vmsacserver.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
public class CreateEntranceDto {

    @NotBlank(message = "EntranceName must not be empty")
    private String entranceName;

    private String entranceDesc;

    private Boolean isActive;

    private List<AccessGroupOnlyDto> accessGroups;

    public Entrance toEntrance(Boolean deleted){
        return new Entrance(null,entranceName,entranceDesc,isActive,deleted);
    }
}
