package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventActionInputTypeCreateDto {

    @NotBlank
    @NotNull
    private String eventActionInputTypeName;

    @NotNull
    private Boolean timerEnabled;

    private String eventActionInputTypeConfig;
}
