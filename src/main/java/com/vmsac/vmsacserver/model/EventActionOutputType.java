package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="eventactionoutputtype")
@Builder
public class EventActionOutputType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventactionoutputid", columnDefinition = "serial")
    private Long eventActionOutputId;

    @Column(name = "eventactionoutputname")
    @NotNull
    @NotBlank
    private String eventActionOutputName;

    @Column(name = "timerenabled")
    @NotNull
    private Boolean timerEnabled;

    @Column(name = "eventactionoutputconfig")
    private String eventActionOutputConfig;
}
