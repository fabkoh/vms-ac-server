package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.AccessGroupScheduleRepository;
import com.vmsac.vmsacserver.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AccessGroupScheduleService#GetAccessGroupCurrentStatus(Long)}
 * using Mockito.
 *
 * <p>This method evaluates whether any of an access group's schedules are currently
 * active by delegating date expansion to
 * {@link ControllerService#GetAccessGroupScheduleObjectWithTime}, then checking
 * whether today's date key is present in the returned map and the current time
 * falls within the window.
 *
 * <p>Covered cases:
 * <ul>
 *   <li>No schedules at all → returns {@code false}</li>
 *   <li>Schedule with full-day window for today → returns {@code true}</li>
 *   <li>Schedule present but date map only contains yesterday → returns {@code false}</li>
 * </ul>
 *
 * No Spring context — pure Mockito unit tests.
 */
@ExtendWith(MockitoExtension.class)
class AccessGroupScheduleServiceTest {

    @InjectMocks
    private AccessGroupScheduleService accessGroupScheduleService;

    @Mock
    private AccessGroupRepository accessGroupRepository;

    @Mock
    private AccessGroupScheduleRepository accessGroupScheduleRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ControllerService controllerService;

    @Mock
    private AccessGroupEntranceService accessGroupEntranceService;

    @Mock
    private AccessGroupEntranceNtoNRepository accessGroupEntranceRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // -----------------------------------------------------------------------
    // GetAccessGroupCurrentStatus — no schedules → returns false
    // -----------------------------------------------------------------------

    @Test
    void getAccessGroupCurrentStatus_noSchedules_returnsFalse() throws Exception {
        long agId = 1L;
        AccessGroup ag = AccessGroup.builder()
                .accessGroupId(agId).accessGroupName("Group A").deleted(false).build();
        when(accessGroupRepository.findByAccessGroupIdAndDeletedFalse(agId))
                .thenReturn(Optional.of(ag));
        when(accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(agId))
                .thenReturn(List.of());
        when(controllerService.GetAccessGroupScheduleObjectWithTime(anyList()))
                .thenReturn(new HashMap<>());

        Boolean result = accessGroupScheduleService.GetAccessGroupCurrentStatus(agId);

        assertFalse(result, "An access group with no schedules must not be active");
    }

    // -----------------------------------------------------------------------
    // GetAccessGroupCurrentStatus — full-day schedule for today → returns true
    // -----------------------------------------------------------------------

    @Test
    void getAccessGroupCurrentStatus_activeScheduleNow_returnsTrue() throws Exception {
        long agId = 2L;
        long ntonId = 10L;

        AccessGroup ag = AccessGroup.builder()
                .accessGroupId(agId).accessGroupName("Group B").deleted(false).build();
        when(accessGroupRepository.findByAccessGroupIdAndDeletedFalse(agId))
                .thenReturn(Optional.of(ag));

        AccessGroupEntranceNtoN nton = AccessGroupEntranceNtoN.builder()
                .groupToEntranceId(ntonId).deleted(false).build();
        when(accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(agId))
                .thenReturn(List.of(nton));

        AccessGroupSchedule schedule = AccessGroupSchedule.builder()
                .accessGroupScheduleId(100L).groupToEntranceId(ntonId)
                .rrule("DTSTART:20200101T000000Z\nRRULE:FREQ=DAILY;INTERVAL=1")
                .timeStart("00:00").timeEnd("24:00")
                .isActive(true).deleted(false).build();
        when(accessGroupScheduleRepository.findAllByGroupToEntranceIdAndDeletedFalse(ntonId))
                .thenReturn(List.of(schedule));

        // Supply a date map with today's key and a full-day time window
        String today = LocalDate.now().format(DATE_FMT);
        Map<String, Object> dateMap = new HashMap<>();
        dateMap.put(today, List.of(Map.of("starttime", "00:00", "endtime", "24:00")));
        when(controllerService.GetAccessGroupScheduleObjectWithTime(anyList()))
                .thenReturn(dateMap);

        Boolean result = accessGroupScheduleService.GetAccessGroupCurrentStatus(agId);

        assertTrue(result, "An access group with a full-day schedule for today must be active");
    }

    // -----------------------------------------------------------------------
    // GetAccessGroupCurrentStatus — date map has only yesterday's key → returns false
    // -----------------------------------------------------------------------

    @Test
    void getAccessGroupCurrentStatus_scheduleDateKeyIsYesterday_returnsFalse() throws Exception {
        long agId = 3L;
        long ntonId = 11L;

        AccessGroup ag = AccessGroup.builder()
                .accessGroupId(agId).accessGroupName("Group C").deleted(false).build();
        when(accessGroupRepository.findByAccessGroupIdAndDeletedFalse(agId))
                .thenReturn(Optional.of(ag));

        AccessGroupEntranceNtoN nton = AccessGroupEntranceNtoN.builder()
                .groupToEntranceId(ntonId).deleted(false).build();
        when(accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(agId))
                .thenReturn(List.of(nton));

        AccessGroupSchedule schedule = AccessGroupSchedule.builder()
                .accessGroupScheduleId(101L).groupToEntranceId(ntonId)
                .rrule("DTSTART:20200101T000000Z\nRRULE:FREQ=DAILY;INTERVAL=1")
                .timeStart("00:00").timeEnd("24:00")
                .isActive(true).deleted(false).build();
        when(accessGroupScheduleRepository.findAllByGroupToEntranceIdAndDeletedFalse(ntonId))
                .thenReturn(List.of(schedule));

        // Supply yesterday's date as the only key — today's key is absent
        String yesterday = LocalDate.now().minusDays(1).format(DATE_FMT);
        Map<String, Object> dateMap = new HashMap<>();
        dateMap.put(yesterday, List.of(Map.of("starttime", "00:00", "endtime", "24:00")));
        when(controllerService.GetAccessGroupScheduleObjectWithTime(anyList()))
                .thenReturn(dateMap);

        Boolean result = accessGroupScheduleService.GetAccessGroupCurrentStatus(agId);

        assertFalse(result, "An access group whose date map does not include today must not be active");
    }
}
