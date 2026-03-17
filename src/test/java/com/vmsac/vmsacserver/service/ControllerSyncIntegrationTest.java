package com.vmsac.vmsacserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.credential.Credential;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import com.vmsac.vmsacserver.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.containsString;

/**
 * Integration tests for {@link ControllerService#generate(Long)}.
 *
 * <p>Verifies what is actually sent over the wire to the Pi's {@code /api/credOccur}
 * endpoint by intercepting the {@link RestTemplate} inside {@code ControllerService}
 * with Spring's {@link MockRestServiceServer}.  No real Pi or HTTP server is needed.
 *
 * <p>Tests run against H2 (profile "test"), full Spring context, rolled-back transaction.
 *
 * <p>Covered contract properties:
 * <ul>
 *   <li>Controller with active auth device + valid credential →
 *       HTTP POST to {@code /api/credOccur} body has non-empty {@code Entrances}
 *       and {@code CredentialLookup} containing the credential UID</li>
 *   <li>Controller with no auth devices → HTTP POST body has
 *       empty {@code Entrances} list and empty {@code CredentialLookup} map</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ControllerSyncIntegrationTest {

    @Autowired
    private ControllerService controllerService;

    @Autowired
    private ControllerRepository controllerRepository;

    @Autowired
    private EntranceRepository entranceRepository;

    @Autowired
    private AuthDeviceRepository authDeviceRepository;

    @Autowired
    private AuthMethodRepository authMethodRepository;

    @Autowired
    private AccessGroupRepository accessGroupRepository;

    @Autowired
    private AccessGroupEntranceNtoNRepository accessGroupEntranceNtoNRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private CredTypeRepository credTypeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private MockRestServiceServer mockServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUpMockServer() throws Exception {
        // ControllerService creates its own RestTemplate in its constructor (not a bean),
        // so we reach it via reflection and bind MockRestServiceServer to that instance.
        Field rtField = ControllerService.class.getDeclaredField("restTemplate");
        rtField.setAccessible(true);
        RestTemplate restTemplate = (RestTemplate) rtField.get(controllerService);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    // -----------------------------------------------------------------------
    // Test 1: full controller → non-empty Entrances + credential in lookup
    // -----------------------------------------------------------------------

    @Test
    void generate_sendsCredOccurPayloadWithCorrectStructure() throws Exception {
        // Build entity graph
        AuthMethod method = authMethodRepository.save(AuthMethod.builder()
                .authMethodDesc("Card").authMethodCondition("Card").deleted(false).build());
        Controller ctrl = controllerRepository.save(Controller.builder()
                .controllerIP("127.0.0.1").controllerSerialNo("SYNC-FULL01")
                .controllerName("Sync Test Pi").controllerIPStatic(false)
                .masterController(false).deleted(false)
                .authDevices(new ArrayList<>()).eventsManagements(new ArrayList<>()).build());
        Entrance ent = entranceRepository.save(Entrance.builder()
                .entranceName("E1-SYNC01").isActive(true).deleted(false)
                .used(false).thirdPartyOption("N.A.").build());
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());

        AccessGroup group = accessGroupRepository.save(AccessGroup.builder()
                .accessGroupName("SyncGroup01").isActive(true).deleted(false).build());
        accessGroupEntranceNtoNRepository.save(AccessGroupEntranceNtoN.builder()
                .accessGroup(group).entrance(ent).deleted(false).build());
        Person person = personRepository.save(Person.builder()
                .personFirstName("Alice").personLastName("Sync")
                .personUid("UID-SYNC01").deleted(false).accessGroup(group).build());
        CredentialType credType = credTypeRepository.save(CredentialType.builder()
                .credTypeName("Card").credTypeDesc("Card").deleted(false).build());
        credentialRepository.save(Credential.builder()
                .credUid("SYNC-CRED-001").credTTL(LocalDateTime.of(2099, 12, 31, 23, 59))
                .isValid(true).isPerm(false).credType(credType).person(person).deleted(false).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        // Capture the body sent to the Pi
        AtomicReference<byte[]> capturedBytes = new AtomicReference<>();
        mockServer.expect(requestTo(containsString("/api/credOccur")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> capturedBytes.set(
                        ((MockClientHttpRequest) request).getBodyAsBytes()))
                .andRespond(withSuccess());

        controllerService.generate(ctrl.getControllerId());
        mockServer.verify();

        @SuppressWarnings("unchecked")
        Map<String, Object> body = objectMapper.readValue(capturedBytes.get(), Map.class);
        List<?> entrances = (List<?>) body.get("Entrances");
        @SuppressWarnings("unchecked")
        Map<String, Object> credLookup = (Map<String, Object>) body.get("CredentialLookup");

        assertFalse(entrances.isEmpty(),
                "generate() must send a non-empty Entrances list when controller has active auth devices");
        assertTrue(credLookup.containsKey("SYNC-CRED-001"),
                "generate() must include valid credential UID in CredentialLookup");
    }

    // -----------------------------------------------------------------------
    // Test 2: empty controller → Entrances:[] CredentialLookup:{}
    // -----------------------------------------------------------------------

    @Test
    void generate_emptyController_sendsValidButEmptyPayload() throws Exception {
        Controller ctrl = controllerRepository.save(Controller.builder()
                .controllerIP("127.0.0.1").controllerSerialNo("SYNC-EMPTY01")
                .controllerName("Empty Sync Pi").controllerIPStatic(false)
                .masterController(false).deleted(false)
                .authDevices(new ArrayList<>()).eventsManagements(new ArrayList<>()).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        AtomicReference<byte[]> capturedBytes = new AtomicReference<>();
        mockServer.expect(requestTo(containsString("/api/credOccur")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> capturedBytes.set(
                        ((MockClientHttpRequest) request).getBodyAsBytes()))
                .andRespond(withSuccess());

        controllerService.generate(ctrl.getControllerId());
        mockServer.verify();

        @SuppressWarnings("unchecked")
        Map<String, Object> body = objectMapper.readValue(capturedBytes.get(), Map.class);
        List<?> entrances = (List<?>) body.get("Entrances");
        @SuppressWarnings("unchecked")
        Map<String, Object> credLookup = (Map<String, Object>) body.get("CredentialLookup");

        assertTrue(entrances.isEmpty(),
                "generate() must send an empty Entrances list for a controller with no auth devices");
        assertTrue(credLookup.isEmpty(),
                "generate() must send an empty CredentialLookup for a controller with no persons");
    }

    // -----------------------------------------------------------------------
    // sendEntranceNameRelationship wire tests
    // -----------------------------------------------------------------------

    @Test
    void sendEntranceNameRelationship_sendsCorrectEntranceIds() throws Exception {
        AuthMethod method = authMethodRepository.save(AuthMethod.builder()
                .authMethodDesc("Card").authMethodCondition("Card").deleted(false).build());
        Controller ctrl = controllerRepository.save(Controller.builder()
                .controllerIP("127.0.0.1").controllerSerialNo("ENR-WIRE01")
                .controllerName("ENR Wire Pi").controllerIPStatic(false)
                .masterController(false).deleted(false)
                .authDevices(new ArrayList<>()).eventsManagements(new ArrayList<>()).build());
        Entrance ent1 = entranceRepository.save(Entrance.builder()
                .entranceName("E1-WIRE01").isActive(true).deleted(false)
                .used(false).thirdPartyOption("N.A.").build());
        Entrance ent2 = entranceRepository.save(Entrance.builder()
                .entranceName("E2-WIRE01").isActive(true).deleted(false)
                .used(false).thirdPartyOption("N.A.").build());
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent1).build());
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E2_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent2).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        AtomicReference<byte[]> capturedBytes = new AtomicReference<>();
        mockServer.expect(requestTo(containsString("/api/entrance-name")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> capturedBytes.set(
                        ((MockClientHttpRequest) request).getBodyAsBytes()))
                .andRespond(withSuccess());

        controllerService.sendEntranceNameRelationship(ctrl.getControllerId());
        mockServer.verify();

        @SuppressWarnings("unchecked")
        Map<String, Object> body = objectMapper.readValue(capturedBytes.get(), Map.class);
        assertEquals("ENR-WIRE01", body.get("controllerSerialNo"));
        assertEquals(ent1.getEntranceId().intValue(), ((Number) body.get("E1")).intValue(),
                "E1 must be entrance 1's ID");
        assertEquals(ent2.getEntranceId().intValue(), ((Number) body.get("E2")).intValue(),
                "E2 must be entrance 2's ID");
    }

    @Test
    void sendEntranceNameRelationship_missingE2Device_sendsEmptyStringForE2() throws Exception {
        AuthMethod method = authMethodRepository.save(AuthMethod.builder()
                .authMethodDesc("Card").authMethodCondition("Card").deleted(false).build());
        Controller ctrl = controllerRepository.save(Controller.builder()
                .controllerIP("127.0.0.1").controllerSerialNo("ENR-WIRE02")
                .controllerName("ENR Wire Pi 2").controllerIPStatic(false)
                .masterController(false).deleted(false)
                .authDevices(new ArrayList<>()).eventsManagements(new ArrayList<>()).build());
        Entrance ent1 = entranceRepository.save(Entrance.builder()
                .entranceName("E1-WIRE02").isActive(true).deleted(false)
                .used(false).thirdPartyOption("N.A.").build());
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent1).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        AtomicReference<byte[]> capturedBytes = new AtomicReference<>();
        mockServer.expect(requestTo(containsString("/api/entrance-name")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> capturedBytes.set(
                        ((MockClientHttpRequest) request).getBodyAsBytes()))
                .andRespond(withSuccess());

        controllerService.sendEntranceNameRelationship(ctrl.getControllerId());
        mockServer.verify();

        @SuppressWarnings("unchecked")
        Map<String, Object> body = objectMapper.readValue(capturedBytes.get(), Map.class);
        assertEquals(ent1.getEntranceId().intValue(), ((Number) body.get("E1")).intValue());
        assertEquals("", body.get("E2"),
                "E2 must be empty string when no E2_IN device is configured");
    }

    @Test
    void sendEntranceNameRelationship_emptyController_sendsBothEmpty() throws Exception {
        Controller ctrl = controllerRepository.save(Controller.builder()
                .controllerIP("127.0.0.1").controllerSerialNo("ENR-WIRE03")
                .controllerName("ENR Wire Pi 3").controllerIPStatic(false)
                .masterController(false).deleted(false)
                .authDevices(new ArrayList<>()).eventsManagements(new ArrayList<>()).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        AtomicReference<byte[]> capturedBytes = new AtomicReference<>();
        mockServer.expect(requestTo(containsString("/api/entrance-name")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> capturedBytes.set(
                        ((MockClientHttpRequest) request).getBodyAsBytes()))
                .andRespond(withSuccess());

        controllerService.sendEntranceNameRelationship(ctrl.getControllerId());
        mockServer.verify();

        @SuppressWarnings("unchecked")
        Map<String, Object> body = objectMapper.readValue(capturedBytes.get(), Map.class);
        assertEquals("", body.get("E1"),
                "E1 must be empty string when controller has no auth devices");
        assertEquals("", body.get("E2"),
                "E2 must be empty string when controller has no auth devices");
        assertEquals("ENR-WIRE03", body.get("controllerSerialNo"));
    }
}
