package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="outputevent")
@Builder
public class OutputEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outputeventid", columnDefinition = "serial")
    private Long outputEventId;

    @Column(name = "timerduration")
    @Positive
    private Integer timerDuration;

    @ManyToOne
    @JoinColumn(name = "eventactionoutputid")
    @NotNull
    private EventActionOutputType eventActionOutputType;
}
