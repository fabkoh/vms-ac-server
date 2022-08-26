package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsManagementPiDto {

    private Long eventsManagementId;

    private String eventsManagementName;

    private List<InputEvent> inputEvents;

    private List<OutputEvent> outputActions;

    private Map triggerSchedule;

    private EntranceIdOnly entrance;

    private ControllerIdOnly controller;

    public static EntranceIdOnly getEntranceId(EventsManagement em) {
        return new EntranceIdOnly(em.getEntrance().getEntranceId());
    }

    public static ControllerIdOnly getControllerId(EventsManagement em) {
        return new ControllerIdOnly(em.getController().getControllerId());
    }
}

@NoArgsConstructor
@AllArgsConstructor
@Data
class EntranceIdOnly {
    private Long entranceId;
}

@NoArgsConstructor
@AllArgsConstructor
@Data
class ControllerIdOnly {
    private Long controllerId;
}

