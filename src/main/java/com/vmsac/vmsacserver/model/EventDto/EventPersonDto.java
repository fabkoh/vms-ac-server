package com.vmsac.vmsacserver.model.EventDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPersonDto {

    @NotNull(message = "personId cannot be NULL")
    private Long personId;

    private String personFirstName;

    private String personLastName;

    @NotNull
    private Boolean deleted;
}
