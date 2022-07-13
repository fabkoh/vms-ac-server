package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsManagementCreateDto {

    @NotNull
    @NotBlank
    private String eventsManagementName;

    @NotNull
    @NotEmpty
    private List<InputEvent> inputEvents;

    @NotNull
    @NotEmpty
    private List<OutputEvent> outputEvents;

    private Long entranceId;

    private Long controllerId;

    private List<TriggerSchedules> triggerSchedules;
}
