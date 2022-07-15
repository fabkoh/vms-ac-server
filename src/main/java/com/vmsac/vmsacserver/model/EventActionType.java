package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="eventactiontype")
@Builder
public class EventActionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventactiontypeid", columnDefinition = "serial")
    private Long eventActionTypeId;

    @Column(name="eventactiontypename")
    private String eventActionTypeName;

    @Column(name="istimerenabled")
    private Boolean isTimerEnabled;
}