package com.vmsac.vmsacserver.model.EventDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventEntranceDto {

    private Long entranceId;

    private String entranceName;

    private Boolean deleted;
}
