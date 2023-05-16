package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsManagementCreateDto {

    @NotNull
    @NotBlank
    private String eventsManagementName;

    @NotNull
    @NotEmpty
    private List<InputEvent> inputEvents;

    @NotNull
    @NotEmpty
    private List<OutputEvent> outputActions;

    private List<Integer> entranceIds;

    private List<Integer> controllerIds;

    private List<TriggerSchedules> triggerSchedules;

    private EventsManagementEmailCreateDto eventsManagementEmail;

    private EventsManagementSMSCreateDto eventsManagementSMS;
}
