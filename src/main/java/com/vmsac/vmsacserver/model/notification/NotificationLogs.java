package com.vmsac.vmsacserver.model.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmsac.vmsacserver.model.Entrance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "notificationlogs")
@Builder
public class NotificationLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificationlogsid", columnDefinition = "serial")
    private Long notificationLogsId;

    @Column(name = "notificationlogsstatuscode")
    private Integer notificationLogsStatusCode;

    @Column(name = "notificationlogserror")
    private String notificationLogsError;

    @Column(name = "timesent")
    private String timeSent;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "eventsmanagementnotificationid")
    @JsonIgnore
    private EventsManagementNotification eventsManagementNotification;

}
