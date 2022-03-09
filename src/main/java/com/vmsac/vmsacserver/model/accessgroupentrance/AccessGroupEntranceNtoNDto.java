package com.vmsac.vmsacserver.model.accessgroupentrance;

import com.vmsac.vmsacserver.model.AccessGroupOnlyDto;
import com.vmsac.vmsacserver.model.Entrance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccessGroupEntranceNtoNDto {
    private Long groupToEntranceId;
    private Entrance entrance;
    private AccessGroupOnlyDto accessGroup;
}
