package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AuthDevice;
import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import com.vmsac.vmsacserver.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthDeviceService#findCurrentAuthMethod(Long)} using Mockito.
 *
 * <p>This method is the server-side mirror of what the Pi does in {@code reader_detects_bits}:
 * it selects the active {@link AuthMethodSchedule} for a device based on the current
 * day of week and time window, falling back to the device's {@code defaultAuthMethod}
 * when no schedule is active.
 *
 * <p>Time-sensitivity is handled by building RRULE strings that include today's
 * day-of-week abbreviation and a 24-hour window, ensuring the tests pass regardless
 * of when they run.
 *
 * <p>Covered cases:
 * <ul>
 *   <li>No schedules at all → returns {@code defaultAuthMethod}</li>
 *   <li>Active schedule matching today's day and the current time → returns that method</li>
 *   <li>Schedule for a different day → returns {@code defaultAuthMethod}</li>
 * </ul>
 *
 * No Spring context — pure Mockito unit tests.
 */
@ExtendWith(MockitoExtension.class)
class AuthDeviceServiceTest {

    @InjectMocks
    private AuthDeviceService authDeviceService;

    @Mock
    private AuthMethodRepository authMethodRepository;

    @Mock
    private AuthDeviceRepository authDeviceRepository;

    @Mock
    private ControllerService controllerService;

    @Mock
    private EntranceService entranceService;

    @Mock
    private AuthMethodScheduleRepository authMethodScheduleRepository;

    @Mock
    private InputEventRepository inputEventRepo;

    @Mock
    private OutputEventRepository outputEventRepo;

    @Mock
    private GENConfigsRepository genRepo;

    @Mock
    private EntityManager em;

    // -----------------------------------------------------------------------
    // findCurrentAuthMethod — no schedules → falls back to defaultAuthMethod
    // -----------------------------------------------------------------------

    @Test
    void findCurrentAuthMethod_noSchedules_returnsDefaultMethod() {
        long deviceId = 1L;
        when(authMethodScheduleRepository.findByAuthDevice_AuthDeviceIdAndDeletedFalse(deviceId))
                .thenReturn(List.of());

        AuthMethod defaultMethod = AuthMethod.builder()
                .authMethodId(1L).authMethodDesc("Card").authMethodCondition("Card").deleted(false).build();
        AuthDevice device = mock(AuthDevice.class);
        when(device.getDefaultAuthMethod()).thenReturn(defaultMethod);
        when(authDeviceRepository.findById(deviceId)).thenReturn(Optional.of(device));

        String result = authDeviceService.findCurrentAuthMethod(deviceId);

        assertEquals("Card", result,
                "When no schedules exist, defaultAuthMethod must be returned");
    }

    // -----------------------------------------------------------------------
    // findCurrentAuthMethod — active schedule for today → returns scheduled method
    // -----------------------------------------------------------------------

    @Test
    void findCurrentAuthMethod_activeScheduleForToday_returnsScheduledMethod() {
        long deviceId = 2L;

        // Build a schedule that is always active: BYDAY = today's day-of-week abbrev,
        // time window 00:00–24:00 (the endtime "24:00" branch is handled by the service).
        String todayAbbr = LocalDate.now().getDayOfWeek().toString().substring(0, 2);
        String rrule = "DTSTART:20200101T000000Z\nRRULE:FREQ=WEEKLY;BYDAY=" + todayAbbr;

        AuthMethod scheduledMethod = AuthMethod.builder()
                .authMethodId(2L).authMethodDesc("Pin").authMethodCondition("Pin").deleted(false).build();

        AuthMethodSchedule activeSchedule = AuthMethodSchedule.builder()
                .authMethodScheduleId(100L)
                .rrule(rrule)
                .timeStart("00:00")
                .timeEnd("24:00")       // entire day → always active
                .isActive(true)
                .deleted(false)
                .authMethod(scheduledMethod)
                .build();

        when(authMethodScheduleRepository.findByAuthDevice_AuthDeviceIdAndDeletedFalse(deviceId))
                .thenReturn(List.of(activeSchedule));

        String result = authDeviceService.findCurrentAuthMethod(deviceId);

        assertEquals("Pin", result,
                "Active schedule for today must override defaultAuthMethod");
        // authDeviceRepository.findById is NOT called (early return from loop)
        verify(authDeviceRepository, never()).findById(deviceId);
    }

    // -----------------------------------------------------------------------
    // findCurrentAuthMethod — schedule for a different day → falls back to default
    // -----------------------------------------------------------------------

    @Test
    void findCurrentAuthMethod_scheduleForDifferentDay_returnsDefaultMethod() {
        long deviceId = 3L;

        // Use "SU,MO,TU,WE,TH,FR,SA" minus today — find a day that is NOT today
        String todayAbbr = LocalDate.now().getDayOfWeek().toString().substring(0, 2);
        // Pick any other day abbreviation
        String otherDayAbbr = todayAbbr.equals("MO") ? "TU" : "MO";
        String rrule = "DTSTART:20200101T000000Z\nRRULE:FREQ=WEEKLY;BYDAY=" + otherDayAbbr;

        AuthMethod scheduledMethod = AuthMethod.builder()
                .authMethodId(3L).authMethodDesc("Fingerprint").authMethodCondition("Fingerprint")
                .deleted(false).build();
        AuthMethodSchedule otherDaySched = AuthMethodSchedule.builder()
                .authMethodScheduleId(101L)
                .rrule(rrule)
                .timeStart("09:00")
                .timeEnd("17:00")
                .isActive(true).deleted(false)
                .authMethod(scheduledMethod)
                .build();

        when(authMethodScheduleRepository.findByAuthDevice_AuthDeviceIdAndDeletedFalse(deviceId))
                .thenReturn(List.of(otherDaySched));

        AuthMethod defaultMethod = AuthMethod.builder()
                .authMethodId(1L).authMethodDesc("Card").authMethodCondition("Card").deleted(false).build();
        AuthDevice device = mock(AuthDevice.class);
        when(device.getDefaultAuthMethod()).thenReturn(defaultMethod);
        when(authDeviceRepository.findById(deviceId)).thenReturn(Optional.of(device));

        String result = authDeviceService.findCurrentAuthMethod(deviceId);

        assertEquals("Card", result,
                "Schedule for a different day must not override defaultAuthMethod");
    }
}
