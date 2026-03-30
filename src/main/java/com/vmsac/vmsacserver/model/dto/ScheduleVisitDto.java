package com.vmsac.vmsacserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduleVisitDto {

    @NotBlank
    private String visitorUid;

    @NotNull
    private LocalDate visitDate;

    private String purpose;
}
