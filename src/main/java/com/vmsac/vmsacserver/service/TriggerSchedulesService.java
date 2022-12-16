package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.EventsManagement;
import com.vmsac.vmsacserver.model.TriggerSchedules;
import com.vmsac.vmsacserver.model.TriggerSchedulesCreateDto;
import com.vmsac.vmsacserver.repository.EventsManagementRepository;
import com.vmsac.vmsacserver.repository.TriggerSchedulesRepository;
import com.vmsac.vmsacserver.util.DateTimeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TriggerSchedulesService {

    @Autowired
    TriggerSchedulesRepository triggerSchedulesRepository;

    @Autowired
    EventsManagementRepository eventsManagementRepository;

    @Autowired
    DateTimeParser dateTimeParser;

    public Optional<TriggerSchedules> create(TriggerSchedulesCreateDto dto) {

//        LocalTime timeStart = dateTimeParser.toLocalTime(dto.getTimeStart());
//        LocalTime timeEnd = dateTimeParser.toLocalTime(dto.getTimeEnd());

        Optional<EventsManagement> opEm = eventsManagementRepository.findByDeletedFalseAndEventsManagementId(dto.getEventsManagementId());

        if (opEm.isPresent()) {
            return Optional.of(triggerSchedulesRepository.save(
                    new TriggerSchedules(null, dto.getTriggerName(),
                            dto.getRrule(), dto.getTimeStart(), dto.getTimeEnd(), false, opEm.get(),
                            dto.getDtstart(),dto.getUntil(),dto.getCount(),dto.getRepeatToggle(),
                            dto.getInterval(),dto.getByweekday(),dto.getBymonthday(),
                            dto.getBysetpos(),dto.getBymonth(),dto.getAllDay(),dto.getEndOfDay())
                    )
            );
        }
        return Optional.empty();
    }
}
