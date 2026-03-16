package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import com.vmsac.vmsacserver.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link ControllerService#deleteControllerWithId(Long)}.
 *
 * <p>Verifies the cascade soft-delete behaviour on H2 (profile "test"):
 * <ul>
 *   <li>Controller row is marked {@code deleted=true} and its name reset to the serial number.</li>
 *   <li>All {@link AuthMethodSchedule} records attached to the controller's devices
 *       are marked {@code deleted=true}.</li>
 *   <li>Calling with a non-existent controller ID throws {@link RuntimeException}.</li>
 * </ul>
 *
 * Each test runs in a rolled-back transaction so the H2 database remains clean.
 * AuthDevice rows require a non-null Entrance FK, so an Entrance is created as a
 * prerequisite wherever an AuthDevice is needed.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ControllerDeleteIntegrationTest {

    @Autowired
    private ControllerService controllerService;

    @Autowired
    private ControllerRepository controllerRepository;

    @Autowired
    private AuthDeviceRepository authDeviceRepository;

    @Autowired
    private AuthMethodRepository authMethodRepository;

    @Autowired
    private AuthMethodScheduleRepository authMethodScheduleRepository;

    @Autowired
    private EntranceRepository entranceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // -----------------------------------------------------------------------
    // deleteControllerWithId — soft-deletes the controller
    // -----------------------------------------------------------------------

    @Test
    void deleteControllerWithId_softDeletesController() throws Exception {
        Controller ctrl = controllerRepository.save(Controller.builder()
                .controllerIP("127.0.0.1").controllerSerialNo("DEL-TEST01")
                .controllerName("Del Test Pi 01").controllerIPStatic(false)
                .masterController(false).deleted(false)
                .authDevices(new ArrayList<>()).eventsManagements(new ArrayList<>()).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        controllerService.deleteControllerWithId(ctrl.getControllerId());

        entityManager.flush();
        entityManager.clear();

        Controller refreshed = controllerRepository.findById(ctrl.getControllerId())
                .orElseThrow(() -> new AssertionError("Controller must still exist in DB"));

        assertTrue(refreshed.getDeleted(), "Controller must be soft-deleted");
        assertEquals(ctrl.getControllerSerialNo(), refreshed.getControllerName(),
                "Controller name must be reset to its serial number on delete");
    }

    // -----------------------------------------------------------------------
    // deleteControllerWithId — cascades to AuthMethodSchedule rows
    // -----------------------------------------------------------------------

    @Test
    void deleteControllerWithId_softDeletesAuthMethodSchedules() throws Exception {
        AuthMethod method = authMethodRepository.save(AuthMethod.builder()
                .authMethodDesc("Card").authMethodCondition("Card").deleted(false).build());

        Controller ctrl = controllerRepository.save(Controller.builder()
                .controllerIP("127.0.0.1").controllerSerialNo("DEL-TEST02")
                .controllerName("Del Test Pi 02").controllerIPStatic(false)
                .masterController(false).deleted(false)
                .authDevices(new ArrayList<>()).eventsManagements(new ArrayList<>()).build());

        // AuthDevice requires a non-null Entrance FK in this schema
        Entrance ent = entranceRepository.save(Entrance.builder()
                .entranceName("DEL-ENT02").isActive(true).deleted(false)
                .used(true).thirdPartyOption("N.A.").build());

        AuthDevice device = authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());

        // Create an auth method schedule linked to the device
        AuthMethodSchedule schedule = authMethodScheduleRepository.save(
                AuthMethodSchedule.builder()
                        .authMethodScheduleName("Sched-DEL02")
                        .rrule("DTSTART:20200101T000000Z\nRRULE:FREQ=DAILY;INTERVAL=1")
                        .timeStart("00:00").timeEnd("24:00")
                        .isActive(true).deleted(false)
                        .authDevice(device).authMethod(method).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        controllerService.deleteControllerWithId(ctrl.getControllerId());

        entityManager.flush();
        entityManager.clear();

        AuthMethodSchedule refreshedSched = authMethodScheduleRepository
                .findById(schedule.getAuthMethodScheduleId())
                .orElseThrow(() -> new AssertionError("Schedule must still exist in DB"));

        assertTrue(refreshedSched.getDeleted(),
                "AuthMethodSchedule must be soft-deleted when its controller is deleted");
    }

    // -----------------------------------------------------------------------
    // deleteControllerWithId — throws for non-existent ID
    // -----------------------------------------------------------------------

    @Test
    void deleteControllerWithId_nonExistentId_throws() {
        assertThrows(RuntimeException.class,
                () -> controllerService.deleteControllerWithId(Long.MAX_VALUE),
                "Should throw when the controller does not exist");
    }
}
