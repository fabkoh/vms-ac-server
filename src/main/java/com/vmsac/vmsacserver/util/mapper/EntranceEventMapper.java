package com.vmsac.vmsacserver.util.mapper;

import com.vmsac.vmsacserver.model.EntranceEvent;
import com.vmsac.vmsacserver.model.EntranceEventDto;
import com.vmsac.vmsacserver.repository.EntranceRepository;
import com.vmsac.vmsacserver.util.DateTimeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class EntranceEventMapper {

    @Autowired
    private DateTimeParser dateTimeParser;

    public LocalDateTime getTime(EntranceEventDto dto) {
        return dateTimeParser.toLocalDateTime(dto.getEventTime());
    }
}
