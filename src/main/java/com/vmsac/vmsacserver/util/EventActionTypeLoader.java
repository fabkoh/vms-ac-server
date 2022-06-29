package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.repository.EntranceEventTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EventActionTypeLoader implements CommandLineRunner {
    private final EntranceEventTypeRepository eventActionTypeRepository;

    public EventActionTypeLoader(EntranceEventTypeRepository eventActionTypeRepository) {
        this.eventActionTypeRepository = eventActionTypeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (eventActionTypeRepository.findAll().isEmpty()) loadData();
    }

    private void loadData() {
        com.vmsac.vmsacserver.model.EntranceEventType authenticated_scan = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("Authenticated Scans")
                        .build()
        );

        com.vmsac.vmsacserver.model.EntranceEventType masterpassword_used = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("Masterpassword used")
                        .build()
        );

        com.vmsac.vmsacserver.model.EntranceEventType unauthenticated_scan = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("UnAuthenticated Scans")
                        .build()
        );

        com.vmsac.vmsacserver.model.EntranceEventType door_opened = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("Door Opened")
                        .build()
        );

        com.vmsac.vmsacserver.model.EntranceEventType door_closed = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("Door Closed")
                        .build()
        );

        com.vmsac.vmsacserver.model.EntranceEventType door_opened_warning = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("Warning : Door Opened without authorisation")
                        .build()
        );

        com.vmsac.vmsacserver.model.EntranceEventType buzzer_start = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("Buzzer Started buzzing")
                        .build()
        );

        com.vmsac.vmsacserver.model.EntranceEventType buzzer_end = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("Buzzer Stopped buzzing")
                        .build()
        );


        com.vmsac.vmsacserver.model.EntranceEventType push_button_pressed = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("Push Button pressed")
                        .build()
        );


        com.vmsac.vmsacserver.model.EntranceEventType GEN_IN_1 = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("GEN_IN_1 triggered")
                        .build()
        );

        com.vmsac.vmsacserver.model.EntranceEventType GEN_IN_2 = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("GEN_IN_2 triggered")
                        .build()
        );

        com.vmsac.vmsacserver.model.EntranceEventType GEN_IN_3 = eventActionTypeRepository.save(
                com.vmsac.vmsacserver.model.EntranceEventType.builder()
                        .actionTypeName("GEN_IN_3 triggered")
                        .build()
        );


    }
}
