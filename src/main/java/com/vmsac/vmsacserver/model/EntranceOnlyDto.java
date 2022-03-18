package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntranceOnlyDto {

    @Positive(message = "entranceId must be greater than 1")
    @NotNull(message = "entranceId must not be empty")
    private Long entranceId;

    @NotNull(message = "EntranceName must not be empty")
    private String entranceName;

    private String entranceDesc;

    private Boolean isActive;

    public Entrance toEntrance(Boolean deleted){
        return new Entrance(entranceId,entranceName,entranceDesc, isActive, deleted);
    }
}
