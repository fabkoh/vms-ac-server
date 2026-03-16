package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import com.vmsac.vmsacserver.model.credential.Credential;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import com.vmsac.vmsacserver.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link ControllerService#createJsonDocument} — the method
 * that produces the JSON document POSTed to each Pi at {@code /api/credOccur}.
 *
 * <p>The method is private, so it is called via reflection using
 * {@code Method.setAccessible(true)} (see {@code invokeCreateJsonDocument}).
 * Tests run against a real H2 in-memory database (profile "test") and are wrapped
 * in a transaction that rolls back after each test.
 *
 * <p>Verified Pi sync contract properties:
 * <ul>
 *   <li>Top-level keys "Entrances" (List) and "CredentialLookup" (Map) always present</li>
 *   <li>Empty controller → empty Entrances list and empty CredentialLookup map</li>
 *   <li>Controller with an active AuthDevice → non-empty Entrances list</li>
 *   <li>{@code masterpin=true} → {@code Masterpassword} is the string {@code "666666"}</li>
 *   <li>{@code masterpin=false} → {@code Masterpassword} is {@code Boolean.FALSE}</li>
 *   <li>Active {@link com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule}
 *       → appears in the AuthMethod list with "Method" and "Schedule" keys</li>
 *   <li>Only credentials where {@code isValid=true} appear in CredentialLookup;
 *       each entry has PersonId, IsPerm, EndDate, AccessGroup keys</li>
 *   <li>Controller with IN + OUT auth devices → both directions present in AuthenticationDevices</li>
 *   <li>Two active AccessGroups linked to one entrance → both appear in AccessGroups list</li>
 *   <li>AccessGroup with {@code isActive=false} → excluded from AccessGroups even if NtoN exists</li>
 *   <li>NtoN join with {@code deleted=true} → excluded from AccessGroups</li>
 *   <li>Person with {@code deleted=true} → excluded from Persons list inside AccessGroup</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ControllerServiceTest {

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
    private AuthMethodScheduleRepository authMethodScheduleRepository;

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

    // ---- existing tests (empty-controller contract) ----

    @Test
    void createJsonDocument_emptyController_hasRequiredTopLevelKeys() throws Exception {
        Controller controller = controllerRepository.save(buildTestController("TEST001"));

        Map<String, Object> doc = invokeCreateJsonDocument(controller);

        assertTrue(doc.containsKey("Entrances"), "credOccur must have 'Entrances' key");
        assertTrue(doc.containsKey("CredentialLookup"), "credOccur must have 'CredentialLookup' key");
        assertTrue(doc.get("Entrances") instanceof List, "'Entrances' must be a List");
        assertTrue(doc.get("CredentialLookup") instanceof Map, "'CredentialLookup' must be a Map");
    }

    @Test
    void createJsonDocument_emptyController_entrancesListIsEmpty() throws Exception {
        Controller controller = controllerRepository.save(buildTestController("TEST002"));

        List<?> entrances = (List<?>) invokeCreateJsonDocument(controller).get("Entrances");

        assertTrue(entrances.isEmpty(),
                "Controller with no auth devices must produce an empty Entrances list");
    }

    @Test
    void createJsonDocument_emptyController_credentialLookupIsEmpty() throws Exception {
        Controller controller = controllerRepository.save(buildTestController("TEST003"));

        Map<?, ?> lookup = (Map<?, ?>) invokeCreateJsonDocument(controller).get("CredentialLookup");

        assertTrue(lookup.isEmpty(),
                "Controller with no entrances/persons must produce an empty CredentialLookup");
    }

    // ---- new tests ----

    @Test
    void createJsonDocument_masterpinTrue_masterpasswordIsString() throws Exception {
        AuthMethod method = saveCardMethod();
        Controller ctrl = saveController("TEST-MP01");
        Entrance ent = saveEntrance("E1-MP01");
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(true)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        Map<String, Object> doc = invokeCreateJsonDocument(ctrl);

        assertEquals("666666", getInDevice(doc).get("Masterpassword"),
                "masterpin=true must produce string '666666' as Masterpassword");
    }

    @Test
    void createJsonDocument_masterpinFalse_masterpasswordIsBooleanFalse() throws Exception {
        AuthMethod method = saveCardMethod();
        Controller ctrl = saveController("TEST-MP02");
        Entrance ent = saveEntrance("E1-MP02");
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        Map<String, Object> doc = invokeCreateJsonDocument(ctrl);

        assertEquals(Boolean.FALSE, getInDevice(doc).get("Masterpassword"),
                "masterpin=false must produce Boolean.FALSE as Masterpassword");
    }

    @Test
    void createJsonDocument_activeAuthMethodSchedule_appearsInAuthMethodList() throws Exception {
        AuthMethod method = saveCardMethod();
        Controller ctrl = saveController("TEST-AMS01");
        Entrance ent = saveEntrance("E1-AMS01");
        AuthDevice device = authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());
        authMethodScheduleRepository.save(AuthMethodSchedule.builder()
                .authMethodScheduleName("Sched-AMS01")
                .rrule("DTSTART:20200101T000000Z\nRRULE:FREQ=DAILY;INTERVAL=1")
                .timeStart("00:00").timeEnd("24:00")
                .isActive(true).deleted(false)
                .authDevice(device).authMethod(method).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        Map<String, Object> doc = invokeCreateJsonDocument(ctrl);

        List<?> authMethodList = (List<?>) getInDevice(doc).get("AuthMethod");
        assertEquals(1, authMethodList.size(),
                "AuthMethod list must contain exactly one scheduled auth method");
        Map<?, ?> entry = (Map<?, ?>) authMethodList.get(0);
        assertTrue(entry.containsKey("Method"), "AuthMethod entry must have 'Method' key");
        assertTrue(entry.containsKey("Schedule"), "AuthMethod entry must have 'Schedule' key");
    }

    @Test
    void createJsonDocument_validAndInvalidCredential_onlyValidInLookup() throws Exception {
        AuthMethod method = saveCardMethod();
        Controller ctrl = saveController("TEST-CRED01");
        Entrance ent = saveEntrance("E1-CRED01");
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());

        AccessGroup group = accessGroupRepository.save(AccessGroup.builder()
                .accessGroupName("TestGroup-CRED01").isActive(true).deleted(false).build());
        accessGroupEntranceNtoNRepository.save(AccessGroupEntranceNtoN.builder()
                .accessGroup(group).entrance(ent).deleted(false).build());

        Person person = personRepository.save(Person.builder()
                .personFirstName("Test").personLastName("User")
                .personUid("UID-CRED01").deleted(false).accessGroup(group).build());

        CredentialType credType = credTypeRepository.save(CredentialType.builder()
                .credTypeName("Card").credTypeDesc("Card").deleted(false).build());
        LocalDateTime ttl = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

        credentialRepository.save(Credential.builder()
                .credUid("VALID-CRED-001").credTTL(ttl).isValid(true).isPerm(false)
                .credType(credType).person(person).deleted(false).build());
        credentialRepository.save(Credential.builder()
                .credUid("INVALID-CRED-002").credTTL(ttl).isValid(false).isPerm(false)
                .credType(credType).person(person).deleted(false).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        Map<String, Object> doc = invokeCreateJsonDocument(ctrl);

        Map<?, ?> lookup = (Map<?, ?>) doc.get("CredentialLookup");
        assertEquals(1, lookup.size(), "Only valid credentials must appear in CredentialLookup");
        assertTrue(lookup.containsKey("VALID-CRED-001"), "Valid credential UID must be in lookup");
        assertFalse(lookup.containsKey("INVALID-CRED-002"), "Invalid credential must be excluded");
        Map<?, ?> credEntry = (Map<?, ?>) lookup.get("VALID-CRED-001");
        assertTrue(credEntry.containsKey("PersonId"), "Credential entry must have PersonId key");
        assertTrue(credEntry.containsKey("IsPerm"), "Credential entry must have IsPerm key");
        assertTrue(credEntry.containsKey("EndDate"), "Credential entry must have EndDate key");
        assertTrue(credEntry.containsKey("AccessGroup"), "Credential entry must have AccessGroup key");
    }

    @Test
    void createJsonDocument_inactiveAccessGroup_excludedFromAccessGroups() throws Exception {
        AuthMethod method = saveCardMethod();
        Controller ctrl = saveController("TEST-IACT01");
        Entrance ent = saveEntrance("E1-IACT01");
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());

        // Active group → must appear; inactive group → must be excluded
        AccessGroup activeGroup = accessGroupRepository.save(AccessGroup.builder()
                .accessGroupName("Active-IACT01").isActive(true).deleted(false).build());
        AccessGroup inactiveGroup = accessGroupRepository.save(AccessGroup.builder()
                .accessGroupName("Inactive-IACT01").isActive(false).deleted(false).build());
        accessGroupEntranceNtoNRepository.save(AccessGroupEntranceNtoN.builder()
                .accessGroup(activeGroup).entrance(ent).deleted(false).build());
        accessGroupEntranceNtoNRepository.save(AccessGroupEntranceNtoN.builder()
                .accessGroup(inactiveGroup).entrance(ent).deleted(false).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        Map<String, Object> doc = invokeCreateJsonDocument(ctrl);
        List<?> accessGroups = getAccessGroups(doc);

        assertEquals(1, accessGroups.size(),
                "isActive=false access group must be excluded from AccessGroups");
        assertEquals(activeGroup.getAccessGroupId(),
                ((Map<?, ?>) accessGroups.get(0)).get("GroupId"),
                "Only the active group must appear");
    }

    @Test
    void createJsonDocument_softDeletedNtoN_excludedFromAccessGroups() throws Exception {
        AuthMethod method = saveCardMethod();
        Controller ctrl = saveController("TEST-DNTN01");
        Entrance ent = saveEntrance("E1-DNTN01");
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());

        AccessGroup group = accessGroupRepository.save(AccessGroup.builder()
                .accessGroupName("Group-DNTN01").isActive(true).deleted(false).build());
        // deleted=true NtoN — the group must not appear in the output
        accessGroupEntranceNtoNRepository.save(AccessGroupEntranceNtoN.builder()
                .accessGroup(group).entrance(ent).deleted(true).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        Map<String, Object> doc = invokeCreateJsonDocument(ctrl);
        List<?> accessGroups = getAccessGroups(doc);

        assertTrue(accessGroups.isEmpty(),
                "Soft-deleted NtoN join must exclude the group from AccessGroups");
    }

    @Test
    void createJsonDocument_softDeletedPerson_excludedFromPersonsList() throws Exception {
        AuthMethod method = saveCardMethod();
        Controller ctrl = saveController("TEST-DPER01");
        Entrance ent = saveEntrance("E1-DPER01");
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());

        AccessGroup group = accessGroupRepository.save(AccessGroup.builder()
                .accessGroupName("Group-DPER01").isActive(true).deleted(false).build());
        accessGroupEntranceNtoNRepository.save(AccessGroupEntranceNtoN.builder()
                .accessGroup(group).entrance(ent).deleted(false).build());

        // One active person, one soft-deleted person — only active must appear
        personRepository.save(Person.builder()
                .personFirstName("Active").personLastName("User")
                .personUid("UID-DPER01-A").deleted(false).accessGroup(group).build());
        Person deletedPerson = personRepository.save(Person.builder()
                .personFirstName("Deleted").personLastName("User")
                .personUid("UID-DPER01-D").deleted(true).accessGroup(group).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        Map<String, Object> doc = invokeCreateJsonDocument(ctrl);
        List<?> persons = (List<?>) ((Map<?, ?>) getAccessGroups(doc).get(0)).get("Persons");

        assertEquals(1, persons.size(),
                "Soft-deleted person must be excluded from the Persons list");
        assertFalse(persons.contains(deletedPerson.getPersonId()),
                "Deleted person's ID must not appear in Persons list");
    }

    @Test
    void createJsonDocument_inAndOutAuthDevices_bothDirectionsPresent() throws Exception {
        AuthMethod method = saveCardMethod();
        Controller ctrl = saveController("TEST-DIR01");
        Entrance ent = saveEntrance("E1-DIR01");
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_OUT").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        Map<String, Object> doc = invokeCreateJsonDocument(ctrl);

        List<?> entrances = (List<?>) doc.get("Entrances");
        assertFalse(entrances.isEmpty());
        Map<?, ?> details = (Map<?, ?>) ((Map<?, ?>) entrances.get(0)).get("EntranceDetails");
        Map<?, ?> authDevices = (Map<?, ?>) details.get("AuthenticationDevices");
        assertTrue(authDevices.containsKey("IN"),
                "AuthenticationDevices must have 'IN' key when E1_IN device exists");
        assertTrue(authDevices.containsKey("OUT"),
                "AuthenticationDevices must have 'OUT' key when E1_OUT device exists");
    }

    @Test
    void createJsonDocument_twoAccessGroups_bothAppearInEntranceDetails() throws Exception {
        AuthMethod method = saveCardMethod();
        Controller ctrl = saveController("TEST-AG01");
        Entrance ent = saveEntrance("E1-AG01");
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent).build());

        AccessGroup groupA = accessGroupRepository.save(AccessGroup.builder()
                .accessGroupName("GroupA-AG01").isActive(true).deleted(false).build());
        AccessGroup groupB = accessGroupRepository.save(AccessGroup.builder()
                .accessGroupName("GroupB-AG01").isActive(true).deleted(false).build());

        accessGroupEntranceNtoNRepository.save(AccessGroupEntranceNtoN.builder()
                .accessGroup(groupA).entrance(ent).deleted(false).build());
        accessGroupEntranceNtoNRepository.save(AccessGroupEntranceNtoN.builder()
                .accessGroup(groupB).entrance(ent).deleted(false).build());

        personRepository.save(Person.builder()
                .personFirstName("Alice").personLastName("A")
                .personUid("UID-AG01-A").deleted(false).accessGroup(groupA).build());
        personRepository.save(Person.builder()
                .personFirstName("Bob").personLastName("B")
                .personUid("UID-AG01-B").deleted(false).accessGroup(groupB).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        Map<String, Object> doc = invokeCreateJsonDocument(ctrl);

        List<?> entrances = (List<?>) doc.get("Entrances");
        Map<?, ?> details = (Map<?, ?>) ((Map<?, ?>) entrances.get(0)).get("EntranceDetails");
        List<?> accessGroups = (List<?>) details.get("AccessGroups");
        assertEquals(2, accessGroups.size(),
                "Both active access groups must appear in EntranceDetails.AccessGroups");

        Set<Object> groupIds = accessGroups.stream()
                .map(ag -> ((Map<?, ?>) ag).get("GroupId"))
                .collect(Collectors.toSet());
        assertTrue(groupIds.contains(groupA.getAccessGroupId()), "Group A must appear in AccessGroups");
        assertTrue(groupIds.contains(groupB.getAccessGroupId()), "Group B must appear in AccessGroups");
    }

    // ---- getEntranceNameRelationship unit tests ----

    @Test
    void getEntranceNameRelationship_bothDirections_returnsCorrectEntranceIds() throws Exception {
        AuthMethod method = saveCardMethod();
        Controller ctrl = saveController("ENR-BOTH01");
        Entrance ent1 = saveEntrance("E1-ENR01");
        Entrance ent2 = saveEntrance("E2-ENR01");
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent1).build());
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E2_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent2).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        Map<String, Object> result = controllerService.getEntranceNameRelationship(ctrl);

        assertEquals(ctrl.getControllerSerialNo(), result.get("controllerSerialNo"));
        assertEquals(ent1.getEntranceId(), result.get("E1"));
        assertEquals(ent2.getEntranceId(), result.get("E2"));
    }

    @Test
    void getEntranceNameRelationship_missingE2Device_returnsEmptyStringForE2() throws Exception {
        AuthMethod method = saveCardMethod();
        Controller ctrl = saveController("ENR-E1ONLY01");
        Entrance ent1 = saveEntrance("E1-ENR02");
        authDeviceRepository.save(AuthDevice.builder()
                .authDeviceDirection("E1_IN").masterpin(false)
                .defaultAuthMethod(method).controller(ctrl).entrance(ent1).build());

        entityManager.flush();
        entityManager.refresh(ctrl);

        Map<String, Object> result = controllerService.getEntranceNameRelationship(ctrl);

        assertEquals(ent1.getEntranceId(), result.get("E1"),
                "E1 must be the entrance ID when E1_IN device exists");
        assertEquals("", result.get("E2"),
                "E2 must be empty string when no E2_IN device exists");
    }

    @Test
    void getEntranceNameRelationship_noDevices_returnsBothEmpty() throws Exception {
        Controller ctrl = saveController("ENR-EMPTY01");

        Map<String, Object> result = controllerService.getEntranceNameRelationship(ctrl);

        assertEquals("", result.get("E1"),
                "E1 must be empty string when no E1_IN device exists");
        assertEquals("", result.get("E2"),
                "E2 must be empty string when no E2_IN device exists");
        assertEquals(ctrl.getControllerSerialNo(), result.get("controllerSerialNo"));
    }

    // ---- helpers ----

    private Controller buildTestController(String serialNo) {
        return Controller.builder()
                .controllerIP("127.0.0.1")
                .controllerSerialNo(serialNo)
                .controllerName("Test Pi " + serialNo)
                .controllerIPStatic(false)
                .masterController(false)
                .deleted(false)
                .authDevices(new ArrayList<>())
                .eventsManagements(new ArrayList<>())
                .build();
    }

    private Controller saveController(String serialNo) {
        return controllerRepository.save(Controller.builder()
                .controllerIP("127.0.0.1")
                .controllerSerialNo(serialNo)
                .controllerName("Test Pi " + serialNo)
                .controllerIPStatic(false)
                .masterController(false)
                .deleted(false)
                .authDevices(new ArrayList<>())
                .eventsManagements(new ArrayList<>())
                .build());
    }

    private AuthMethod saveCardMethod() {
        return authMethodRepository.save(AuthMethod.builder()
                .authMethodDesc("Card").authMethodCondition("Card").deleted(false).build());
    }

    private Entrance saveEntrance(String name) {
        return entranceRepository.save(Entrance.builder()
                .entranceName(name).isActive(true).deleted(false).used(false)
                .thirdPartyOption("N.A.").build());
    }

    @SuppressWarnings("unchecked")
    private List<?> getAccessGroups(Map<String, Object> doc) {
        List<?> entrances = (List<?>) doc.get("Entrances");
        assertFalse(entrances.isEmpty(), "Entrances must not be empty");
        Map<?, ?> details = (Map<?, ?>) ((Map<?, ?>) entrances.get(0)).get("EntranceDetails");
        return (List<?>) details.get("AccessGroups");
    }

    @SuppressWarnings("unchecked")
    private Map<?, ?> getInDevice(Map<String, Object> doc) {
        List<?> entrances = (List<?>) doc.get("Entrances");
        assertFalse(entrances.isEmpty(), "Entrances must not be empty");
        Map<?, ?> entranceData = (Map<?, ?>) entrances.get(0);
        Map<?, ?> details = (Map<?, ?>) entranceData.get("EntranceDetails");
        Map<?, ?> authDevicesMap = (Map<?, ?>) details.get("AuthenticationDevices");
        return (Map<?, ?>) authDevicesMap.get("IN");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> invokeCreateJsonDocument(Controller controller) throws Exception {
        Method method = ControllerService.class.getDeclaredMethod("createJsonDocument", Controller.class);
        method.setAccessible(true);
        return (Map<String, Object>) method.invoke(controllerService, controller);
    }
}
