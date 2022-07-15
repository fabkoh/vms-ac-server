package com.vmsac.vmsacserver.model;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.EventDto.EventControllerDto;
import com.vmsac.vmsacserver.model.EventDto.EventEntranceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsManagementDto {

    private Long eventsManagementId;

    private String eventsManagementName;

    private List<InputEvent> inputEvents;

    private List<OutputEvent> outputActions;

    private List<TriggerSchedules> triggerSchedules;

    private EventEntranceDto entrance;

    private EventControllerDto controller;
}
