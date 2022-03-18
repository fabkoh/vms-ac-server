package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessGroupOnlyDto {

    @Positive(message = "AccessGroupId must be greater than 1")
    @NotNull(message = "AccessGroupId must not be empty")
    private Long accessGroupId;

    @NotNull(message = "AccessGroupName must not be empty")
    private String accessGroupName;

    private String accessGroupDesc;

    public AccessGroup toAccessGroup(Boolean deleted){
        return new AccessGroup(accessGroupId,accessGroupName,accessGroupDesc, deleted, null);
    }
}
