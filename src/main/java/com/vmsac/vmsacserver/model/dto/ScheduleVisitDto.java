package com.vmsac.vmsacserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduleVisitDto {

    @NotBlank
    private String visitorUid;

    private String purpose;
}
