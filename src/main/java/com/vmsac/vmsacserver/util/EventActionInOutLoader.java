package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.EventActionInputType;
import com.vmsac.vmsacserver.model.EventActionOutputType;
import com.vmsac.vmsacserver.repository.EventActionInputTypeRepository;
import com.vmsac.vmsacserver.repository.EventActionOutputTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EventActionInOutLoader implements CommandLineRunner {

    private final EventActionInputTypeRepository inputTypeRepository;

    private final EventActionOutputTypeRepository outputTypeRepository;

    public EventActionInOutLoader(EventActionInputTypeRepository inputTypeRepository,
                                 EventActionOutputTypeRepository outputTypeRepository) {
        this.inputTypeRepository = inputTypeRepository;
        this.outputTypeRepository = outputTypeRepository;
    }

    public void run(String... args) throws Exception {
        if (inputTypeRepository.findAll().isEmpty()) loadInputData();
        if (outputTypeRepository.findAll().isEmpty()) loadOutputData();
    }

    private void loadInputData() {
        // 1
        EventActionInputType AUTHENTICATED_SCAN = inputTypeRepository.save(
                EventActionInputType.builder()
                        .eventActionInputName("AUTHENTICATED_SCAN")
                        .timerEnabled(false)
                        .build()
        );

        // 2
        EventActionInputType UNAUTHENTICATED_SCAN = inputTypeRepository.save(
                EventActionInputType.builder()
                        .eventActionInputName("UNAUTHENTICATED_SCAN")
                        .timerEnabled(false)
                        .build()
        );

        // 3
        EventActionInputType EXIT_BUTTON_PRESSED = inputTypeRepository.save(
                EventActionInputType.builder()
                        .eventActionInputName("EXIT_BUTTON_PRESSED")
                        .timerEnabled(false)
                        .build()
        );

        // 4
        EventActionInputType CONTACT_OPEN_WITHOUT_AUTHENTICATION = inputTypeRepository.save(
                EventActionInputType.builder()
                        .eventActionInputName("CONTACT_OPEN_WITHOUT_AUTHENTICATION")
                        .timerEnabled(true)
                        .build()
        );

        // 5
        EventActionInputType CONTACT_OPEN_WITH_AUTHENTICATION = inputTypeRepository.save(
                EventActionInputType.builder()
                        .eventActionInputName("CONTACT_OPEN_WITH_AUTHENTICATION")
                        .timerEnabled(true)
                        .build()
        );

        // 6
        EventActionInputType FIRE = inputTypeRepository.save(
                EventActionInputType.builder()
                        .eventActionInputName("FIRE")
                        .timerEnabled(false)
                        .build()
        );

        // 7
        EventActionInputType GEN_IN_1 = inputTypeRepository.save(
                EventActionInputType.builder()
                        .eventActionInputName("GEN_IN_1")
                        .timerEnabled(false)
                        .build()
        );

        // 8
        EventActionInputType GEN_IN_2 = inputTypeRepository.save(
                EventActionInputType.builder()
                        .eventActionInputName("GEN_IN_2")
                        .timerEnabled(false)
                        .build()
        );

        // 9
        EventActionInputType GEN_IN_3 = inputTypeRepository.save(
                EventActionInputType.builder()
                        .eventActionInputName("GEN_IN_3")
                        .timerEnabled(false)
                        .build()
        );

        // 10
        EventActionInputType CONTACT_CLOSE = inputTypeRepository.save(
                EventActionInputType.builder()
                        .eventActionInputName("CONTACT_CLOSE")
                        .timerEnabled(false)
                        .build()
        );
    }

    private void loadOutputData() {
        // 1
        EventActionOutputType GEN_OUT_1 = outputTypeRepository.save(
                EventActionOutputType.builder()
                        .eventActionOutputName("GEN_OUT_1")
                        .timerEnabled(true)
                        .build()
        );

        // 2
        EventActionOutputType GEN_OUT_2 = outputTypeRepository.save(
                EventActionOutputType.builder()
                        .eventActionOutputName("GEN_OUT_2")
                        .timerEnabled(true)
                        .build()
        );

        // 3
        EventActionOutputType GEN_OUT_3 = outputTypeRepository.save(
                EventActionOutputType.builder()
                        .eventActionOutputName("GEN_OUT_3")
                        .timerEnabled(true)
                        .build()
        );

        // 4
        EventActionOutputType EMLOCK_1 = outputTypeRepository.save(
                EventActionOutputType.builder()
                        .eventActionOutputName("EMLOCK_1")
                        .timerEnabled(false)
                        .build()
        );

        // 5
        EventActionOutputType EMLOCK_2 = outputTypeRepository.save(
                EventActionOutputType.builder()
                        .eventActionOutputName("EMLOCK_2")
                        .timerEnabled(false)
                        .build()
        );

        // 6
        EventActionOutputType BUZZER = outputTypeRepository.save(
                EventActionOutputType.builder()
                        .eventActionOutputName("BUZZER")
                        .timerEnabled(true)
                        .build()
        );

        // 7
        EventActionOutputType LED = outputTypeRepository.save(
                EventActionOutputType.builder()
                        .eventActionOutputName("LED")
                        .timerEnabled(true)
                        .build()
        );

        // 8
        EventActionOutputType NOTIFICATION = outputTypeRepository.save(
                EventActionOutputType.builder()
                        .eventActionOutputName("NOTIFICATION")
                        .timerEnabled(false)
                        .build()
        );
    }
}