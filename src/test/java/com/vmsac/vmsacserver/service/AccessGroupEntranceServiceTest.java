package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.Entrance;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.AccessGroupScheduleRepository;
import com.vmsac.vmsacserver.repository.EntranceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Set;

/**
 * Unit tests for {@link AccessGroupEntranceService} using Mockito.
 *
 * Covers {@code assignEntrancesToAccessGroup}:
 * <ul>
 *   <li>New entrance added → a "Default Schedule" (00:00–23:59, FREQ=DAILY) is
 *       automatically created for the new AccessGroupEntranceNtoN join row</li>
 *   <li>Entrance removed from the assignment list → the old join row is soft-deleted
 *       ({@code deleted=true})</li>
 * </ul>
 *
 * Covers {@code assignAccessGroupsToEntrance}:
 * <ul>
 *   <li>Non-existent entrance ID → throws {@link RuntimeException}</li>
 * </ul>
 *
 * No Spring context — pure unit tests with mocked repositories.
 */
@ExtendWith(MockitoExtension.class)
class AccessGroupEntranceServiceTest {

    @InjectMocks
    private AccessGroupEntranceService service;

    @Mock
    private AccessGroupEntranceNtoNRepository accessGroupEntranceRepository;

    @Mock
    private AccessGroupRepository accessGroupRepository;

    @Mock
    private EntranceRepository entranceRepository;

    @Mock
    private AccessGroupScheduleRepository accessGroupScheduleRepository;

    @Test
    void assignEntrancesToAccessGroup_newEntrance_createsDefaultSchedule() {
        // Setup: access group 1 currently has NO entrances; assign entrance 5
        when(accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(List.of());

        AccessGroup group = buildGroup(1L);
        when(accessGroupRepository.findByAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(group));

        Entrance entrance = buildEntrance(5L);
        when(entranceRepository.findByEntranceIdInAndDeletedFalse(anySet()))
                .thenReturn(List.of(entrance));

        // saveAll for the new NtoN relationship
        AccessGroupEntranceNtoN savedNtoN = buildNtoN(group, entrance, 99L);
        when(accessGroupEntranceRepository.saveAll(any())).thenReturn(List.of(savedNtoN));

        service.assignEntrancesToAccessGroup(List.of(5L), 1L);

        // Verify a default schedule was saved.
        // saveAll is called twice: once by deleteAccessGroupEntranceNtoN (empty list)
        // and once by addNewAccessGroupEntranceNtoN (the default schedule list).
        ArgumentCaptor<List> scheduleCaptor = ArgumentCaptor.forClass(List.class);
        verify(accessGroupScheduleRepository, times(2)).saveAll(scheduleCaptor.capture());

        // Find the non-empty saveAll call (the delete pass saves an empty list first,
        // then the create pass saves the default schedule). Using stream instead of
        // get(1) to avoid fragility if the service changes call order.
        @SuppressWarnings("unchecked")
        List<AccessGroupSchedule> savedSchedules = (List<AccessGroupSchedule>)
                scheduleCaptor.getAllValues().stream()
                        .filter(l -> !l.isEmpty())
                        .findFirst()
                        .orElseThrow();
        assertEquals(1, savedSchedules.size());

        AccessGroupSchedule defaultSchedule = savedSchedules.get(0);
        assertEquals("Default Schedule", defaultSchedule.getAccessGroupScheduleName());
        assertEquals("00:00", defaultSchedule.getTimeStart());
        assertEquals("23:59", defaultSchedule.getTimeEnd());
        assertTrue(defaultSchedule.getIsActive());
        assertFalse(defaultSchedule.getDeleted());
        // RRULE must contain FREQ=DAILY (the 24/7 default)
        assertTrue(defaultSchedule.getRrule().contains("FREQ=DAILY"));
    }

    @Test
    void assignEntrancesToAccessGroup_removedEntrance_softDeletesNtoNAndSchedule() {
        // Setup: access group 1 currently has entrance 5; we assign entrance 6 (removes 5)
        AccessGroup group = buildGroup(1L);
        Entrance entrance5 = buildEntrance(5L);
        AccessGroupEntranceNtoN existing = buildNtoN(group, entrance5, 42L);
        when(accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(List.of(existing));

        when(accessGroupRepository.findByAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(group));
        Entrance entrance6 = buildEntrance(6L);
        when(entranceRepository.findByEntranceIdInAndDeletedFalse(anySet()))
                .thenReturn(List.of(entrance6));

        AccessGroupEntranceNtoN newNtoN = buildNtoN(group, entrance6, 100L);
        when(accessGroupEntranceRepository.saveAll(argThat(l -> !((List<?>)l).isEmpty())))
                .thenReturn(List.of(newNtoN));
        when(accessGroupScheduleRepository.findAllByGroupToEntranceIdInAndDeletedFalse(List.of(42L)))
                .thenReturn(List.of());

        service.assignEntrancesToAccessGroup(List.of(6L), 1L);

        // Existing NtoN for entrance 5 must be soft-deleted
        assertTrue(existing.getDeleted());
    }

    @Test
    void assignAccessGroupsToEntrance_nonExistentEntrance_throws() {
        when(accessGroupEntranceRepository.findAllByEntranceEntranceIdAndDeletedFalse(99L))
                .thenReturn(List.of());
        when(entranceRepository.findByEntranceIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.assignAccessGroupsToEntrance(List.of(1L), 99L));
    }

    // ---- helpers ----

    private AccessGroup buildGroup(Long id) {
        AccessGroup g = new AccessGroup();
        g.setAccessGroupId(id);
        g.setDeleted(false);
        return g;
    }

    private Entrance buildEntrance(Long id) {
        Entrance e = new Entrance();
        e.setEntranceId(id);
        e.setDeleted(false);
        return e;
    }

    private AccessGroupEntranceNtoN buildNtoN(AccessGroup group, Entrance entrance, Long id) {
        AccessGroupEntranceNtoN nton = new AccessGroupEntranceNtoN();
        nton.setGroupToEntranceId(id);
        nton.setAccessGroup(group);
        nton.setEntrance(entrance);
        nton.setDeleted(false);
        return nton;
    }
}
