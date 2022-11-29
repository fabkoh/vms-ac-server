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
public class EventsManagementSMSCreateDto {
    @NotNull
    @NotBlank
    private String eventsManagementSMSRecipients;

    private String eventsManagementSMSContent;

    private Boolean useDefaultSMS;

    @Override
    public String toString() {
        return String.format("eventsManagementSMSRecipients: %s, eventsManagementSMSContent: %s", eventsManagementSMSRecipients, eventsManagementSMSContent);
    }

    public EventsManagementNotification toEventManagementNotification(Boolean deleted, EventsManagement eventsManagement) {
        String name = "";
        if (eventsManagement.getEntrance() != null) {
            name = eventsManagement.getEntrance().getEntranceName();
        } else if (eventsManagement.getController() != null) {
            name = eventsManagement.getController().getControllerName();
        }
        String defaultContent = "Event Management " + eventsManagement.getEventsManagementName() +  " at " + name + " " + eventsManagementSMSContent;
        String content = eventsManagementSMSContent;
        if (useDefaultSMS) {
            content = defaultContent;
        }
        EventsManagementNotification eventsManagementNotification = new EventsManagementNotification(null, "SMS", eventsManagementSMSRecipients, content,
                "", deleted, eventsManagement, new ArrayList<>());
        return eventsManagementNotification;
    }
}
