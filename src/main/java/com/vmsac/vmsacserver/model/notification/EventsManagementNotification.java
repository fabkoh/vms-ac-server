package com.vmsac.vmsacserver.model.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmsac.vmsacserver.model.EventsManagement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "eventsmanagementnotification")
@Builder
public class EventsManagementNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventsmanagementnotificationid", columnDefinition = "serial")
    private Long eventsManagementNotificationId;

    @Column(name = "eventsmanagementnotificationtype")
    private String eventsManagementNotificationType; // SMS, EMAIL

    @Column(name = "eventsmanagementnotificationrecipients")
    private String eventsManagementNotificationRecipients;

    @Column(name = "eventsmanagementnotificationcontent")
    private String eventsManagementNotificationContent;

    @Column(name = "eventsmanagementnotificationtitle")
    private String eventsManagementNotificationTitle;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "eventsmanagementid")
    private EventsManagement eventsManagement;

    @JsonIgnore
    @OneToMany(mappedBy = "eventsManagementNotification", cascade = CascadeType.ALL)
    private List<NotificationLogs> notificationLogs;

    @Override
    public String toString() {
        return String.format("Events management notification id %s, type: %s, content: %s, recipient: %s", eventsManagementNotificationId, eventsManagementNotificationType, eventsManagementNotificationContent, eventsManagementNotificationRecipients);
    }
}
