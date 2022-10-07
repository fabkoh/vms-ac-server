package com.vmsac.vmsacserver.model;

import java.util.ArrayList;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;

public class EventsManagementEmailCreateDto {
    @NotNull
    @NotBlank
    private String eventsManagementEmailRecipients;

    private String eventsManagementEmailContent;

    private String eventsManagementEmailTitle;

    public EventsManagementNotification toEventManagementNotification(Boolean deleted, EventsManagement eventsManagement) {
        return new EventsManagementNotification(null, "SMS", eventsManagementEmailRecipients, eventsManagementEmailContent,
                eventsManagementEmailTitle, deleted, eventsManagement, new ArrayList<>());
    }
}