package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.EventActionType;
import com.vmsac.vmsacserver.repository.EventActionTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "devpostgres"})

public class EventActionTypeLoader implements CommandLineRunner, Ordered {
    private final EventActionTypeRepository eventActionTypeRepository;

    @Override
    public int getOrder() {
        return 1; // Set the desired order value
    }

    public EventActionTypeLoader(EventActionTypeRepository eventActionTypeRepository) {
        this.eventActionTypeRepository = eventActionTypeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (eventActionTypeRepository.findAll().isEmpty()) loadData();
    }

    private void loadData() {
        System.out.println("creating eventActionTypes");
        EventActionType authenticated_scan = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("Authenticated Scans")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType masterpassword_used = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("Masterpassword used")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType unauthenticated_scan = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("UnAuthenticated Scans")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType door_opened = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("Door Opened")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType door_closed = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("Door Closed")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType door_opened_warning = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("Warning : Door Opened without authorisation")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType buzzer_start = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("Buzzer Started buzzing")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType buzzer_end = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("Buzzer Stopped buzzing")
                        .isTimerEnabled(false)
                        .build()
        );


        EventActionType push_button_pressed = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("Push Button pressed")
                        .isTimerEnabled(false)
                        .build()
        );


        EventActionType GEN_IN_1 = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("GEN_IN_1 triggered")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType GEN_IN_2 = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("GEN_IN_2 triggered")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType GEN_IN_3 = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("GEN_IN_3 triggered")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType Valid_PIN_used = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("Valid PIN used")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType Invalid_PIN_used = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("Invalid PIN used")
                        .isTimerEnabled(false)
                        .build()
        );

        EventActionType Fire = eventActionTypeRepository.save(
                EventActionType.builder()
                        .eventActionTypeName("Fire")
                        .isTimerEnabled(false)
                        .build()
        );
    }
}