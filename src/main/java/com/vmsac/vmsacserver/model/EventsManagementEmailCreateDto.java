package com.vmsac.vmsacserver.model;

import java.util.ArrayList;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.vmsac.vmsacserver.model.notification.EventsManagementNotification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsManagementEmailCreateDto {
    @NotNull
    @NotBlank
    private String eventsManagementEmailRecipients;

    private String eventsManagementEmailContent;

    private String eventsManagementEmailTitle;

    public EventsManagementNotification toEventManagementNotification(Boolean deleted, EventsManagement eventsManagement) {
        String name = "";
        if (eventsManagement.getEntrance() != null) {
            name = eventsManagement.getEntrance().getEntranceName();
        } else if (eventsManagement.getController() != null) {
            name = eventsManagement.getController().getControllerName();
        }
        String content = "Event Management " + eventsManagement.getEventsManagementName() +  " at " + name + " " + eventsManagementEmailContent;
        return new EventsManagementNotification(null, "EMAIL", eventsManagementEmailRecipients, content,
                eventsManagementEmailTitle, deleted, eventsManagement, new ArrayList<>());
    }
}