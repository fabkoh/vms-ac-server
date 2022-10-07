package com.vmsac.vmsacserver.model;

import java.util.ArrayList;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;

public class EventsManagementSMSCreateDto {
    @NotNull
    @NotBlank
    private String eventsManagementSMSRecipients;

    private String eventsManagementSMSContent;

    public EventsManagementNotification toEventManagementNotification(Boolean deleted, EventsManagement eventsManagement) {
        return new EventsManagementNotification(null, "SMS", eventsManagementSMSRecipients, eventsManagementSMSContent,
                "", deleted, eventsManagement, new ArrayList<>());
    }
}
