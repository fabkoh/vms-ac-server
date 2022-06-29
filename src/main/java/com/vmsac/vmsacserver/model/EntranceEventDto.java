package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmsac.vmsacserver.util.DateTimeParser;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntranceEventDto {

    @NotNull
    @Positive
    private Long entranceEnventId;

    @NotNull
    @Positive
    private Long entranceId;

    private String direction;

    @NotNull
    private String eventTime;

    private Long personId;

    private Long accessGroupId;

    @NotNull
    @Positive
    private Long eventActionTypeId;

    @NotNull
    @Positive
    private Long authMethodId;

}
