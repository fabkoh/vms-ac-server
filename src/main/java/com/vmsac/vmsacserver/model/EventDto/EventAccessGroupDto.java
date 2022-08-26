package com.vmsac.vmsacserver.model.EventDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventAccessGroupDto {

    private Long accessGroupId;

    private String accessGroupName;

    private Boolean deleted;
}
