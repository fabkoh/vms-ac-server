package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.authmethodschedule.CreateAuthMethodScheduleDto;
import com.vmsac.vmsacserver.repository.AuthDeviceRepository;
import com.vmsac.vmsacserver.repository.AuthMethodRepository;
import com.vmsac.vmsacserver.repository.AuthMethodScheduleRepository;
import com.vmsac.vmsacserver.repository.ControllerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AuthMethodScheduleService#checkNewScheds(List)} using Mockito.
 *
 * <p>{@code checkNewScheds} detects overlapping RRULE schedules before they are persisted:
 * <ol>
 *   <li>Expands each RRULE's {@code BYDAY} clause into an array of day abbreviations.</li>
 *   <li>Pairwise-compares the day arrays ({@code compareRruleArray}).</li>
 *   <li>If two schedules share at least one day, checks whether their time windows
 *       also overlap ({@code compareTime}).</li>
 *   <li>Returns a list of all clashing {@link CreateAuthMethodScheduleDto}s;
 *       an empty list means no conflicts.</li>
 * </ol>
 *
 * Covered cases:
 * <ul>
 *   <li>No shared days → empty clash list</li>
 *   <li>Same day, overlapping time window → both schedules in clash list</li>
 *   <li>Same day, non-overlapping time window → empty clash list</li>
 * </ul>
 *
 * No Spring context — pure unit tests; repository mocks are injected but never called.
 */
@ExtendWith(MockitoExtension.class)
class AuthMethodScheduleServiceTest {

    @InjectMocks
    private AuthMethodScheduleService authMethodScheduleService;

    @Mock
    private AuthMethodScheduleRepository authMethodScheduleRepository;

    @Mock
    private AuthMethodRepository authMethodRepository;

    @Mock
    private AuthDeviceRepository authDeviceRepository;

    @Mock
    private ControllerRepository controllerRepository;

    // -----------------------------------------------------------------------
    // checkNewScheds — no shared days → no clash
    // -----------------------------------------------------------------------

    @Test
    void checkNewScheds_differentDays_returnsEmptyClashList() {
        CreateAuthMethodScheduleDto sched1 = sched("Sched-A",
                "DTSTART:20200101T000000Z\nRRULE:FREQ=WEEKLY;BYDAY=MO,TU",
                "09:00", "12:00");
        CreateAuthMethodScheduleDto sched2 = sched("Sched-B",
                "DTSTART:20200101T000000Z\nRRULE:FREQ=WEEKLY;BYDAY=WE,TH",
                "09:00", "12:00");

        List<CreateAuthMethodScheduleDto> clashes =
                authMethodScheduleService.checkNewScheds(List.of(sched1, sched2));

        assertTrue(clashes.isEmpty(),
                "Schedules on completely different days must not clash");
    }

    // -----------------------------------------------------------------------
    // checkNewScheds — same day, overlapping times → both in clash list
    // -----------------------------------------------------------------------

    @Test
    void checkNewScheds_sameDayOverlappingTimes_returnsBothClashed() {
        // Both cover Monday; time windows 09:00–12:00 and 10:00–15:00 overlap
        CreateAuthMethodScheduleDto sched1 = sched("Sched-C",
                "DTSTART:20200101T000000Z\nRRULE:FREQ=WEEKLY;BYDAY=MO",
                "09:00", "12:00");
        CreateAuthMethodScheduleDto sched2 = sched("Sched-D",
                "DTSTART:20200101T000000Z\nRRULE:FREQ=WEEKLY;BYDAY=MO",
                "10:00", "15:00");

        List<CreateAuthMethodScheduleDto> clashes =
                authMethodScheduleService.checkNewScheds(List.of(sched1, sched2));

        assertEquals(2, clashes.size(),
                "Both schedules must appear in the clash list when they share a day "
                        + "and their time windows overlap");
        assertTrue(clashes.contains(sched1), "sched1 must be in the clash list");
        assertTrue(clashes.contains(sched2), "sched2 must be in the clash list");
    }

    // -----------------------------------------------------------------------
    // checkNewScheds — same day, non-overlapping times → no clash
    // -----------------------------------------------------------------------

    @Test
    void checkNewScheds_sameDayNonOverlappingTimes_returnsEmptyClashList() {
        // Both cover Friday; time windows 09:00–12:00 and 13:00–17:00 do NOT overlap
        CreateAuthMethodScheduleDto sched1 = sched("Sched-E",
                "DTSTART:20200101T000000Z\nRRULE:FREQ=WEEKLY;BYDAY=FR",
                "09:00", "12:00");
        CreateAuthMethodScheduleDto sched2 = sched("Sched-F",
                "DTSTART:20200101T000000Z\nRRULE:FREQ=WEEKLY;BYDAY=FR",
                "13:00", "17:00");

        List<CreateAuthMethodScheduleDto> clashes =
                authMethodScheduleService.checkNewScheds(List.of(sched1, sched2));

        assertTrue(clashes.isEmpty(),
                "Schedules on the same day with non-overlapping time windows must not clash");
    }

    // ---- helpers ----

    private static CreateAuthMethodScheduleDto sched(String name, String rrule,
                                                      String start, String end) {
        CreateAuthMethodScheduleDto dto = new CreateAuthMethodScheduleDto();
        dto.setAuthMethodScheduleName(name);
        dto.setRrule(rrule);
        dto.setTimeStart(start);
        dto.setTimeEnd(end);
        // authMethod / authDevice intentionally null — not accessed by checkNewScheds
        return dto;
    }
}
