package com.vmsac.vmsacserver.model;

import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoNDto;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class CreateEntranceDto {

    @NotBlank(message = "EntranceName must not be empty")
    @NotNull
    private String entranceName;

    private String entranceDesc;

    private Boolean isActive;

    private String thirdPartyOption;

    private AccessGroupEntranceNtoN accessGroupsEntrance;

    public Entrance toEntrance(Boolean deleted){
        return new Entrance(null, entranceName, entranceDesc, true, deleted,
                false, thirdPartyOption,null, null);
    }
}
