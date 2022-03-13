package com.vmsac.vmsacserver.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
public class CreateAccessGroupDto {

    @NotBlank(message = "AccessGroupName must not be empty")
    private String accessGroupName;

    private String accessGroupDesc;

    private List<PersonOnlyDto> persons;
    public AccessGroup toAccessGroup(Boolean deleted){
        return new AccessGroup(null,accessGroupName,accessGroupDesc,deleted,null,null);
    }
}
