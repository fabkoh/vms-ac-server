package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.AuthDevice;
import com.vmsac.vmsacserver.model.Controller;
import com.vmsac.vmsacserver.model.ControllerConnection;
import com.vmsac.vmsacserver.service.AuthDeviceService;
import com.vmsac.vmsacserver.service.ControllerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@code GET /api/controllerConnection/{controllerId}}.
 *
 * <p>{@link ControllerService#getControllerConnectionUnicon} creates its own {@code RestTemplate}
 * instance internally (not the service's field), so {@code MockRestServiceServer} cannot intercept
 * it.  Instead, {@code @MockBean} is used to stub the service, isolating the controller's
 * direction-matching and {@code lastOnline}-update logic from the HTTP layer.
 *
 * <p>Covered correctness properties:
 * <ul>
 *   <li>When Pi is reachable and reports {@code E1_IN: true}, both the controller and the
 *       matching auth device get a non-null {@code lastOnline}; other devices are not updated.</li>
 *   <li>When Pi is unreachable ({@code getControllerConnectionUnicon} returns {@code null}),
 *       the endpoint returns 400 and no {@code save()} is called.</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControllerConnectionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ControllerService controllerService;

    @MockBean
    private AuthDeviceService authDeviceService;

    // -----------------------------------------------------------------------
    // Test 1: Pi online, E1_IN connected → lastOnline set on controller + device
    // -----------------------------------------------------------------------

    @Test
    void getControllerConnection_E1InConnected_setsLastOnlineOnControllerAndMatchingDevice()
            throws Exception {
        AuthDevice e1In = AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false).build();
        AuthDevice e1Out = AuthDevice.builder()
                .authDeviceDirection("E1_OUT").masterpin(false).build();
        List<AuthDevice> devices = new ArrayList<>(List.of(e1In, e1Out));

        Controller ctrl = Controller.builder()
                .controllerId(1L).controllerIP("127.0.0.1")
                .controllerSerialNo("CONN-TEST01").controllerName("Conn Test")
                .controllerIPStatic(false).masterController(false).deleted(false)
                .authDevices(devices).eventsManagements(new ArrayList<>()).build();

        when(controllerService.findById(1L)).thenReturn(Optional.of(ctrl));
        when(controllerService.getControllerConnectionUnicon("127.0.0.1"))
                .thenReturn(ControllerConnection.builder()
                        .E1_IN(true).E1_OUT(false).E2_IN(false).E2_OUT(false).build());

        mockMvc.perform(get("/api/controllerConnection/1"))
                .andExpect(status().isOk());

        // Controller itself must have lastOnline set
        ArgumentCaptor<Controller> ctrlCaptor = ArgumentCaptor.forClass(Controller.class);
        verify(controllerService).save(ctrlCaptor.capture());
        assertNotNull(ctrlCaptor.getValue().getLastOnline(),
                "controller.lastOnline must be set when Pi responds successfully");

        // Only E1_IN device is "Connected" → only it should be saved with lastOnline set
        ArgumentCaptor<AuthDevice> devCaptor = ArgumentCaptor.forClass(AuthDevice.class);
        verify(authDeviceService, times(1)).save(devCaptor.capture());
        assertEquals("E1_IN", devCaptor.getValue().getAuthDeviceDirection(),
                "Only the E1_IN device must be saved when E1_IN=true and E1_OUT=false");
        assertNotNull(devCaptor.getValue().getLastOnline(),
                "E1_IN device.lastOnline must be set when connection reports E1_IN=true");
    }

    // -----------------------------------------------------------------------
    // Test 2: Pi unreachable (null connection) → 400, no save
    // -----------------------------------------------------------------------

    @Test
    void getControllerConnection_piUnreachable_returns400AndSkipsSave() throws Exception {
        Controller ctrl = Controller.builder()
                .controllerId(2L).controllerIP("127.0.0.1")
                .controllerSerialNo("CONN-TEST02").controllerName("Conn Test 2")
                .controllerIPStatic(false).masterController(false).deleted(false)
                .authDevices(new ArrayList<>()).eventsManagements(new ArrayList<>()).build();

        when(controllerService.findById(2L)).thenReturn(Optional.of(ctrl));
        when(controllerService.getControllerConnectionUnicon("127.0.0.1")).thenReturn(null);

        mockMvc.perform(get("/api/controllerConnection/2"))
                .andExpect(status().isBadRequest());

        verify(controllerService, never()).save(any(Controller.class));
        verify(authDeviceService, never()).save(any(AuthDevice.class));
    }
}
