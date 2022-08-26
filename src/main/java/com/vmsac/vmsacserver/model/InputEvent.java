package com.vmsac.vmsacserver.model;

import jdk.jfr.Unsigned;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="inputevent")
@Builder
public class InputEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inputeventid", columnDefinition = "serial")
    private Long inputEventId;

    @Column(name = "timerduration")
    @Positive
    private Integer timerDuration;

    @ManyToOne
    @JoinColumn(name = "eventactioninputid")
    @NotNull
    private EventActionInputType eventActionInputType;
}
