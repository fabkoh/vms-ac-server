package com.vmsac.vmsacserver.model;

import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoNDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
public class CreateEntranceDto {

    @NotBlank(message = "EntranceName must not be empty")
    private String entranceName;

    private String entranceDesc;

    private Boolean isActive;

    private List<AccessGroupEntranceNtoNDto> accessGroupsEntrance;

    public Entrance toEntrance(Boolean deleted){
        if(this.isActive == null) {
            return new Entrance(null,entranceName,entranceDesc,false,deleted, null);
        }
        return new Entrance(null,entranceName,entranceDesc,isActive,deleted, null);
    }
}
