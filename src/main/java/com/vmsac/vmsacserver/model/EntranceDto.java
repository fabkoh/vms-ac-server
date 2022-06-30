package com.vmsac.vmsacserver.model;

import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoNDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntranceDto {

    @Positive(message = "EntranceId must be greater than 1")
    @NotNull(message = "EntranceId must not be empty")
    private Long entranceId;

    @NotNull(message = "EntranceName must not be empty")
    private String entranceName;

    private String entranceDesc;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Boolean used;

    private String thirdPartyOption;

    private List<AccessGroup> accessGroups;

    private List<AuthDevice> entranceAuthDevices;

    public Entrance toEntrance(Boolean deleted){
        return new Entrance(entranceId, entranceName, entranceDesc, isActive, deleted,
                used, thirdPartyOption, entranceAuthDevices);
    }
}
