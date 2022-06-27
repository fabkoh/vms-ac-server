package com.vmsac.vmsacserver.model.EventDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPersonDto {

    private Long personId;

    private String personFirstName;

    private String personLastName;

    private Boolean deleted;
}
