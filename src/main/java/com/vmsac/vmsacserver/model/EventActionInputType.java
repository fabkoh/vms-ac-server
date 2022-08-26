package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="eventactioninputtype")
@Builder
public class EventActionInputType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventactioninputid", columnDefinition = "serial")
    private Long eventActionInputId;

    @Column(name = "eventactioninputname")
    @NotNull
    @NotBlank
    private String eventActionInputName;

    @Column(name = "timerenabled")
    @NotNull
    private Boolean timerEnabled;

    @Column(name = "eventactioninputconfig")
    private String eventActionInputConfig;
}
