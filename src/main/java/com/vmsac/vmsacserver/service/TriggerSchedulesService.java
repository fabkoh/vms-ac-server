package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.repository.EventsManagementRepository;
import com.vmsac.vmsacserver.repository.TriggerSchedulesRepository;
import com.vmsac.vmsacserver.util.DateTimeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TriggerSchedulesService {

    @Autowired
    TriggerSchedulesRepository triggerSchedulesRepository;

    @Autowired
    EventsManagementRepository eventsManagementRepository;

    @Autowired
    DateTimeParser dateTimeParser;


}
