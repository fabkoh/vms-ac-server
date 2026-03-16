package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.AccessGroupScheduleRepository;
import com.vmsac.vmsacserver.repository.PersonRepository;
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

/**
 * Unit tests for {@link AccessGroupService} using Mockito.
 *
 * Covers soft-delete cascade for {@code deleteAccessGroupById}:
 * <ul>
 *   <li>The AccessGroup row is marked {@code deleted=true}</li>
 *   <li>All Persons in the group have their {@code accessGroup} FK nulled out</li>
 *   <li>All AccessGroupEntranceNtoN join rows are marked {@code deleted=true}</li>
 *   <li>All AccessGroupSchedule rows linked to those join rows are marked
 *       {@code deleted=true}</li>
 *   <li>A non-existent group ID throws {@link RuntimeException}</li>
 * </ul>
 *
 * Also covers {@code nameInUse}: returns {@code true} when an active (non-deleted)
 * group with that name already exists, {@code false} otherwise.
 *
 * No Spring context — pure unit tests with mocked repositories.
 */
@ExtendWith(MockitoExtension.class)
class AccessGroupServiceTest {

    @InjectMocks
    private AccessGroupService accessGroupService;

    @Mock
    private AccessGroupRepository accessGroupRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private AccessGroupEntranceNtoNRepository accessGroupEntranceNtoNRepository;

    @Mock
    private AccessGroupScheduleRepository accessGroupScheduleRepository;

    @Test
    void deleteAccessGroupById_softDeletesGroup() throws Exception {
        AccessGroup group = buildGroup(1L);
        when(accessGroupRepository.findByAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(group));
        when(personRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(List.of());
        when(accessGroupEntranceNtoNRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(List.of());
        when(accessGroupScheduleRepository.findAllByGroupToEntranceIdInAndDeletedFalse(any()))
                .thenReturn(List.of());

        accessGroupService.deleteAccessGroupById(1L);

        assertTrue(group.getDeleted());
        verify(accessGroupRepository).save(group);
    }

    @Test
    void deleteAccessGroupById_nullsAccessGroupOnPersons() throws Exception {
        AccessGroup group = buildGroup(1L);
        when(accessGroupRepository.findByAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(group));
        Person p1 = mock(Person.class);
        Person p2 = mock(Person.class);
        when(personRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(List.of(p1, p2));
        when(accessGroupEntranceNtoNRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(List.of());
        when(accessGroupScheduleRepository.findAllByGroupToEntranceIdInAndDeletedFalse(any()))
                .thenReturn(List.of());

        accessGroupService.deleteAccessGroupById(1L);

        verify(p1).setAccessGroup(null);
        verify(p2).setAccessGroup(null);
        verify(personRepository).saveAll(List.of(p1, p2));
    }

    @Test
    void deleteAccessGroupById_softDeletesNtoNRelationships() throws Exception {
        AccessGroup group = buildGroup(1L);
        when(accessGroupRepository.findByAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(group));
        when(personRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(List.of());

        AccessGroupEntranceNtoN nton1 = mock(AccessGroupEntranceNtoN.class);
        when(nton1.getGroupToEntranceId()).thenReturn(10L);
        AccessGroupEntranceNtoN nton2 = mock(AccessGroupEntranceNtoN.class);
        when(nton2.getGroupToEntranceId()).thenReturn(11L);
        when(accessGroupEntranceNtoNRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(List.of(nton1, nton2));
        when(accessGroupScheduleRepository.findAllByGroupToEntranceIdInAndDeletedFalse(any()))
                .thenReturn(List.of());

        accessGroupService.deleteAccessGroupById(1L);

        verify(nton1).setDeleted(true);
        verify(nton2).setDeleted(true);
        verify(accessGroupEntranceNtoNRepository).saveAll(List.of(nton1, nton2));
    }

    @Test
    void deleteAccessGroupById_softDeletesSchedules() throws Exception {
        AccessGroup group = buildGroup(1L);
        when(accessGroupRepository.findByAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(group));
        when(personRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(List.of());

        AccessGroupEntranceNtoN nton = mock(AccessGroupEntranceNtoN.class);
        when(nton.getGroupToEntranceId()).thenReturn(10L);
        when(accessGroupEntranceNtoNRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(1L))
                .thenReturn(List.of(nton));

        AccessGroupSchedule schedule = mock(AccessGroupSchedule.class);
        when(accessGroupScheduleRepository.findAllByGroupToEntranceIdInAndDeletedFalse(List.of(10L)))
                .thenReturn(List.of(schedule));

        accessGroupService.deleteAccessGroupById(1L);

        verify(schedule).setDeleted(true);
        verify(accessGroupScheduleRepository).saveAll(List.of(schedule));
    }

    @Test
    void deleteAccessGroupById_nonExistentGroup_throws() {
        when(accessGroupRepository.findByAccessGroupIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> accessGroupService.deleteAccessGroupById(99L));
    }

    @Test
    void nameInUse_existingName_returnsTrue() {
        when(accessGroupRepository.findByAccessGroupNameAndDeleted("Staff", false))
                .thenReturn(Optional.of(buildGroup(1L)));
        assertTrue(accessGroupService.nameInUse("Staff"));
    }

    @Test
    void nameInUse_unusedName_returnsFalse() {
        when(accessGroupRepository.findByAccessGroupNameAndDeleted("Guests", false))
                .thenReturn(Optional.empty());
        assertFalse(accessGroupService.nameInUse("Guests"));
    }

    // ---- helpers ----

    private AccessGroup buildGroup(Long id) {
        AccessGroup g = new AccessGroup();
        g.setAccessGroupId(id);
        g.setDeleted(false);
        return g;
    }
}
