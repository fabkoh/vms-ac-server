package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AuthDevice;
import com.vmsac.vmsacserver.model.Entrance;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
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

/**
 * Unit tests for {@link EntranceService#delete(Long)} using Mockito.
 *
 * <p>Verifies the cascade soft-delete chain:
 * <ul>
 *   <li>Entrance row is marked {@code deleted=true} and {@code used=false}</li>
 *   <li>Each linked {@link AuthDevice} has its entrance FK nulled via
 *       {@link AuthDeviceService#AuthDeviceEntranceUpdate}</li>
 *   <li>All {@link AccessGroupEntranceNtoN} join rows are marked {@code deleted=true}
 *       and forwarded to {@link AccessGroupEntranceService#deleteAccessGroupEntranceNtoN},
 *       which cascades further to AccessGroupSchedule rows</li>
 * </ul>
 *
 * No Spring context — pure unit tests with mocked repositories and services.
 */
@ExtendWith(MockitoExtension.class)
class EntranceServiceTest {

    @InjectMocks
    private EntranceService entranceService;

    @Mock
    private EntranceRepository entranceRepository;

    @Mock
    private AuthDeviceService authDeviceService;

    @Mock
    private AccessGroupEntranceService accessGroupEntranceService;

    @Mock
    private AccessGroupEntranceNtoNRepository accessGroupEntranceNtoNRepository;

    @Mock
    private AccessGroupRepository AccessGroupRepository;

    @Mock
    private ControllerService controllerService;

    // -----------------------------------------------------------------------
    // delete() — soft-deletes Entrance row
    // -----------------------------------------------------------------------

    @Test
    void delete_softDeletesEntranceRow() {
        Entrance entrance = Entrance.builder()
                .entranceId(1L).entranceName("Main").isActive(true)
                .deleted(false).used(true).thirdPartyOption("N.A.").build();

        when(entranceRepository.findByEntranceIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(entrance));
        when(authDeviceService.findbyEntranceid(1L)).thenReturn(List.of());
        when(accessGroupEntranceNtoNRepository.findAllByEntranceEntranceIdAndDeletedFalse(1L))
                .thenReturn(List.of());
        when(entranceRepository.save(any())).thenReturn(entrance);

        entranceService.delete(1L);

        assertTrue(entrance.getDeleted(), "Entrance must be soft-deleted");
        assertFalse(entrance.getUsed(), "Entrance.used must be set false on delete");
        verify(entranceRepository).save(entrance);
    }

    // -----------------------------------------------------------------------
    // delete() — nulls entrance FK on all linked AuthDevices
    // -----------------------------------------------------------------------

    @Test
    void delete_nullsEntranceOnLinkedAuthDevices() throws Exception {
        Entrance entrance = Entrance.builder()
                .entranceId(2L).entranceName("Side").isActive(true)
                .deleted(false).used(true).thirdPartyOption("N.A.").build();
        AuthDevice dev1 = mock(AuthDevice.class);
        AuthDevice dev2 = mock(AuthDevice.class);

        when(entranceRepository.findByEntranceIdAndDeletedFalse(2L))
                .thenReturn(Optional.of(entrance));
        when(authDeviceService.findbyEntranceid(2L)).thenReturn(List.of(dev1, dev2));
        when(authDeviceService.AuthDeviceEntranceUpdate(any(), isNull()))
                .thenReturn(mock(AuthDevice.class));
        when(accessGroupEntranceNtoNRepository.findAllByEntranceEntranceIdAndDeletedFalse(2L))
                .thenReturn(List.of());
        when(entranceRepository.save(any())).thenReturn(entrance);

        entranceService.delete(2L);

        // Both auth devices must have entrance set to null
        verify(authDeviceService).AuthDeviceEntranceUpdate(dev1, null);
        verify(authDeviceService).AuthDeviceEntranceUpdate(dev2, null);
    }

    // -----------------------------------------------------------------------
    // delete() — soft-deletes NtoN rows and passes them to deleteAccessGroupEntranceNtoN
    // -----------------------------------------------------------------------

    @Test
    void delete_softDeletesNtoNRowsAndForwardsToCascade() {
        Entrance entrance = Entrance.builder()
                .entranceId(3L).entranceName("Back").isActive(true)
                .deleted(false).used(false).thirdPartyOption("N.A.").build();
        AccessGroupEntranceNtoN nton1 = AccessGroupEntranceNtoN.builder()
                .groupToEntranceId(10L).deleted(false).build();
        AccessGroupEntranceNtoN nton2 = AccessGroupEntranceNtoN.builder()
                .groupToEntranceId(11L).deleted(false).build();

        when(entranceRepository.findByEntranceIdAndDeletedFalse(3L))
                .thenReturn(Optional.of(entrance));
        when(authDeviceService.findbyEntranceid(3L)).thenReturn(List.of());
        when(accessGroupEntranceNtoNRepository.findAllByEntranceEntranceIdAndDeletedFalse(3L))
                .thenReturn(List.of(nton1, nton2));
        when(entranceRepository.save(any())).thenReturn(entrance);

        entranceService.delete(3L);

        // Both NtoN rows must have been marked deleted=true before the cascade call
        assertTrue(nton1.getDeleted(), "NtoN row 1 must be soft-deleted");
        assertTrue(nton2.getDeleted(), "NtoN row 2 must be soft-deleted");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AccessGroupEntranceNtoN>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(accessGroupEntranceService).deleteAccessGroupEntranceNtoN(captor.capture());
        assertEquals(2, captor.getValue().size(),
                "Both NtoN rows must be forwarded to deleteAccessGroupEntranceNtoN");
    }
}
