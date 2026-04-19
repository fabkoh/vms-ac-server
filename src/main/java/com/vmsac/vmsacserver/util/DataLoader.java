package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.controller.ControllerController;
import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceSchedule;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import com.vmsac.vmsacserver.model.credential.Credential;
import com.vmsac.vmsacserver.model.videorecorder.VideoRecorder;
import com.vmsac.vmsacserver.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Profile({"dev", "demo"})
//@DependsOn("eventActionTypeLoader")
@Component
public class DataLoader implements CommandLineRunner, Ordered {
    private final AccessGroupRepository accessGroupRepository;
    private final EntranceRepository entranceRepository;
    private final EventRepository eventRepository;
    private final EventActionTypeRepository eventActionTypeRepository;
    private final PersonRepository personRepository;
    private final AccessGroupEntranceNtoNRepository accessGroupEntranceRepository;
    private final AccessGroupScheduleRepository accessGroupScheduleRepository;
    private final EntranceScheduleRepository entranceScheduleRepository;
    private final CredTypeRepository credTypeRepository;
    private final CredentialRepository credentialRepository;
    private final ControllerRepository controllerRepository;
    private final AuthDeviceRepository authDeviceRepository;
    private final ControllerController controllerController;
    private final VideoRecorderRepository videoRecorderRepository;

    @Override
    public int getOrder() {
        return 2; // Set the desired order value
    }

    public DataLoader(AccessGroupRepository accessGroupRepository, EntranceRepository entranceRepository, EventRepository eventRepository, EventActionTypeRepository eventActionTypeRepository, PersonRepository personRepository, AccessGroupEntranceNtoNRepository accessGroupEntranceRepository, AccessGroupScheduleRepository accessGroupScheduleRepository, CredTypeRepository credTypeRepository, CredentialRepository credentialRepository, EntranceScheduleRepository entranceScheduleRepository, ControllerRepository controllerRepository, AuthDeviceRepository authDeviceRepository, ControllerController controllerController, VideoRecorderRepository videoRecorderRepository) {

        this.accessGroupRepository = accessGroupRepository;
        this.entranceRepository = entranceRepository;
        this.eventRepository = eventRepository;
        this.eventActionTypeRepository = eventActionTypeRepository;
        this.personRepository = personRepository;
        this.accessGroupEntranceRepository = accessGroupEntranceRepository;
        this.accessGroupScheduleRepository = accessGroupScheduleRepository;

        this.entranceScheduleRepository = entranceScheduleRepository;

        this.credTypeRepository = credTypeRepository;
        this.credentialRepository = credentialRepository;
        this.controllerRepository = controllerRepository;
        this.authDeviceRepository = authDeviceRepository;
        this.controllerController = controllerController;
        this.videoRecorderRepository = videoRecorderRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadData(); // uncomment to load data listed in loadData() below
    }

    private void loadData() throws ParseException {
        AccessGroup dune = accessGroupRepository.save(
                AccessGroup.builder()
                        .accessGroupName("Dune")
                        .accessGroupDesc("the characters from dune")
                        .isActive(true)
                        .deleted(false)
                        .build()
        );

        AccessGroup notDune = accessGroupRepository.save(
                AccessGroup.builder()
                        .accessGroupName("Not dune")
                        .deleted(false)
                        .isActive(true)
                        .build()
        );

        EventActionType typeAuthenticated  = eventActionTypeRepository.findById(1L).orElse(null);
        EventActionType typeUnauthenticated = eventActionTypeRepository.findById(3L).orElse(null);
        EventActionType typeDoorOpened     = eventActionTypeRepository.findById(4L).orElse(null);
        EventActionType typeValidPin       = eventActionTypeRepository.findById(13L).orElse(null);
        EventActionType typeInvalidPin     = eventActionTypeRepository.findById(14L).orElse(null);


        AccessGroup emptyGroup = accessGroupRepository.save(
                AccessGroup.builder()
                        .accessGroupName("Empty group")
                        .deleted(false)
                        .isActive(true)
                        .build()
        );

//        Controller controller1 = controllerRepository.save(
//                Controller.builder()
//                        .controllerName("controller1")
//                        .controllerIPStatic(Boolean.FALSE)
//                        .controllerIP("111")
//                        .pendingIP("111")
//                        .controllerMAC("111")
//                        .controllerSerialNo("111")
//                        .lastOnline(LocalDateTime.now())
//                        .lastSync(LocalDateTime.now())
//                        .created(LocalDateTime.now())
//                        .masterController(Boolean.FALSE)
//                        .pinAssignmentConfig("111")
//                        .settingsConfig("111")
//                        .deleted(Boolean.FALSE)
//                        .build()
//        );

        Person paulAtreides = personRepository.save(
                Person.builder()
                        .personFirstName("Paul")
                        .personLastName("Atreides")
                        .personUid("lCj7sSpU")
                        .personMobileNumber("+1 (100) 100-1000")
                        .personEmail("paul@atreides.com")
                        .accessGroup(dune)
                        .deleted(false)
                        .build()
        );

        Person letoAtreides = personRepository.save(
                Person.builder()
                        .personFirstName("Leto")
                        .personLastName("Atreides")
                        .personUid("F2VMFevJ")
                        .personEmail("leto@atreides.com")
                        .accessGroup(dune)
                        .deleted(false)
                        .build()
        );

        Person johnSmith = personRepository.save(
                Person.builder()
                        .personFirstName("John")
                        .personLastName("Smith")
                        .personUid("abc")
                        .personMobileNumber("+65 9876-5432")
                        .accessGroup(notDune)
                        .deleted(false)
                        .build()
        );

        Person andyTan = personRepository.save(
                Person.builder()
                        .personFirstName("Andy")
                        .personLastName("Tan")
                        .personUid("123")
                        .deleted(false)
                        .build()
        );

        Entrance mainEntrance = entranceRepository.save(
                Entrance.builder()
                        .entranceName("Main Entrance")
                        .entranceDesc("the main entrance")
                        .isActive(true)
                        .deleted(false)
                        .used(false)
                        .thirdPartyOption("N.A.")
                        .build()
        );

        Entrance sideEntrance = entranceRepository.save(
                Entrance.builder()
                        .entranceName("Side Entrance")
                        .isActive(true)
                        .deleted(false)
                        .used(false)
                        .thirdPartyOption("N.A.")
                        .build()
        );

        Entrance abandonedEntrance = entranceRepository.save(
                Entrance.builder()
                        .entranceName("Abandoned Entrance")
                        .isActive(false)
                        .deleted(false)
                        .used(false)
                        .thirdPartyOption("N.A.")
                        .build()
        );

        AccessGroupEntranceNtoN duneMainEntrance = accessGroupEntranceRepository.save(
                AccessGroupEntranceNtoN.builder()
                        .accessGroup(dune)
                        .entrance(mainEntrance)
                        .deleted(false)
                        .build()
        );

        AccessGroupEntranceNtoN duneSideEntrance = accessGroupEntranceRepository.save(
                AccessGroupEntranceNtoN.builder()
                        .accessGroup(dune)
                        .entrance(sideEntrance)
                        .deleted(false)
                        .build()
        );

        AccessGroupEntranceNtoN notDuneMainEntrance = accessGroupEntranceRepository.save(
                AccessGroupEntranceNtoN.builder()
                        .accessGroup(notDune)
                        .entrance(mainEntrance)
                        .deleted(false)
                        .build()
        );

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMdd");
        String rruleDtstart = "DTSTART:" + dtf.format(LocalDateTime.now()) + "T000000\nRRULE:";

        AccessGroupSchedule duneMainEntranceDefault = accessGroupScheduleRepository.save(
                AccessGroupSchedule.builder()
                        .accessGroupScheduleName("Default Schedule")
                        .rrule(rruleDtstart + "FREQ=DAILY;INTERVAL=1;WKST=MO")
                        .timeStart("00:00")
                        .timeEnd("23:59")
                        .groupToEntranceId(duneMainEntrance.getGroupToEntranceId())
                        .isActive(true)
                        .deleted(false)
                        .build()
        );


        AccessGroupSchedule duneSideEntranceWeekdays = accessGroupScheduleRepository.save(
                AccessGroupSchedule.builder()
                        .accessGroupScheduleName("Weekdays 9 to 5")
                        .rrule(rruleDtstart + "FREQ=WEEKLY;COUNT=200;INTERVAL=1;WKST=MO")
                        .timeStart("09:00")
                        .timeEnd("17:00")
                        .groupToEntranceId(duneSideEntrance.getGroupToEntranceId())
                        .deleted(false)
                        .isActive(true)
                        .build()
        );

        AccessGroupSchedule duneSideEntranceWeekends = accessGroupScheduleRepository.save(
                AccessGroupSchedule.builder()
                        .accessGroupScheduleName("Weekends 12 to 2")
                        .rrule(rruleDtstart + "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=SA,SU")
                        .timeStart("12:00")
                        .timeEnd("17:00")
                        .groupToEntranceId(duneSideEntrance.getGroupToEntranceId())
                        .isActive(true)
                        .deleted(false)
                        .build()
        );

        AccessGroupSchedule notDuneMainEntranceDefault = accessGroupScheduleRepository.save(
                AccessGroupSchedule.builder()
                        .accessGroupScheduleName("Default Schedule")
                        .rrule(rruleDtstart + "FREQ=DAILY;INTERVAL=1;WKST=MO")
                        .timeStart("00:00")
                        .timeEnd("23:59")
                        .groupToEntranceId(notDuneMainEntrance.getGroupToEntranceId())
                        .isActive(true)
                        .deleted(false)
                        .build()
        );


        EntranceSchedule mainEntranceSchedule = entranceScheduleRepository.save(
                EntranceSchedule.builder()
                        .entranceScheduleName("Default Schedule")
                        .rrule(rruleDtstart + "FREQ=DAILY;INTERVAL=1;WKST=MO")
                        .timeStart("00:00")
                        .timeEnd("23:59")
                        .entranceId(mainEntrance.getEntranceId())
                        .deleted(false)
                        .isActive(true)
                        .build()
        );

        EntranceSchedule mainEntranceAddedSchedule = entranceScheduleRepository.save(
                EntranceSchedule.builder()
                        .entranceScheduleName("Added Schedule")
                        .rrule(rruleDtstart + "FREQ=DAILY;INTERVAL=1;WKST=MO")
                        .timeStart("00:00")
                        .timeEnd("12:00")
                        .entranceId(mainEntrance.getEntranceId())
                        .deleted(false)
                        .isActive(true)
                        .build()
        );

//        see authmethodloader for cred type init
        CredentialType cardType = credTypeRepository.save(
                CredentialType.builder()
                        .credTypeName("Card")
                        .credTypeDesc("RFID card")
                        .deleted(false)
                        .build()
        );

        CredentialType pinType = credTypeRepository.save(
                CredentialType.builder()
                        .credTypeName("Pin")
                        .credTypeDesc("digit pin")
                        .deleted(false)
                        .build()
        );

        Credential paulCard = credentialRepository.save(
                Credential.builder()
                        .credUid("123400")
                        .credTTL(LocalDateTime.now())
                        .isValid(true)
                        .isPerm(true)
                        .credType(cardType)
                        .person(paulAtreides)
                        .deleted(false)
                        .build()
        );

        Credential paulPin = credentialRepository.save(
                Credential.builder()
                        .credUid("1234")
                        .credTTL(LocalDateTime.of(2022, 12, 31, 23, 59))
                        .isValid(true)
                        .isPerm(false)
                        .credType(pinType)
                        .person(paulAtreides)
                        .deleted(false)
                        .build()
        );

        Credential paulExpiredCard = credentialRepository.save(
                Credential.builder()
                        .credUid("123401")
                        .credTTL(LocalDateTime.now())
                        .isValid(true)
                        .isPerm(false)
                        .credType(cardType)
                        .person(paulAtreides)
                        .deleted(false)
                        .build()
        );

        Credential paulInvalidCard = credentialRepository.save(
                Credential.builder()
                        .credUid("123402")
                        .credTTL(LocalDateTime.now())
                        .isValid(false)
                        .isPerm(false)
                        .credType(cardType)
                        .person(paulAtreides)
                        .deleted(false)
                        .build()
        );

        Credential letoCard = credentialRepository.save(
                Credential.builder()
                        .credUid("4321")
                        .credTTL(LocalDateTime.now())
                        .isValid(true)
                        .isPerm(true)
                        .credType(cardType)
                        .person(letoAtreides)
                        .deleted(false)
                        .build()
        );

        // ── Controllers ──────────────────────────────────────────────────────
        Controller blockA = controllerRepository.save(
                Controller.builder()
                        .controllerName("Block A Controller")
                        .controllerIPStatic(Boolean.TRUE)
                        .controllerIP("192.168.1.100")
                        .pendingIP("192.168.1.100")
                        .controllerMAC("DC:A6:32:AA:BB:01")
                        .controllerSerialNo("100000001a2b3c4d")
                        .lastOnline(LocalDateTime.now())
                        .lastSync(LocalDateTime.now())
                        .created(LocalDateTime.now())
                        .masterController(Boolean.TRUE)
                        .deleted(Boolean.FALSE)
                        .build()
        );

        Controller blockB = controllerRepository.save(
                Controller.builder()
                        .controllerName("Block B Controller")
                        .controllerIPStatic(Boolean.FALSE)
                        .controllerIP("192.168.1.101")
                        .pendingIP("192.168.1.101")
                        .controllerMAC("DC:A6:32:AA:BB:02")
                        .controllerSerialNo("100000002a2b3c4d")
                        .lastOnline(LocalDateTime.now().minusHours(2))
                        .lastSync(LocalDateTime.now().minusHours(2))
                        .created(LocalDateTime.now())
                        .masterController(Boolean.FALSE)
                        .deleted(Boolean.FALSE)
                        .build()
        );

        // ── Auth Devices ──────────────────────────────────────────────────────
        authDeviceRepository.save(
                AuthDevice.builder()
                        .authDeviceName("Block A – Main IN Reader")
                        .authDeviceDirection("E1_IN")
                        .masterpin(false)
                        .controller(blockA)
                        .entrance(mainEntrance)
                        .lastOnline(LocalDateTime.now())
                        .build()
        );
        authDeviceRepository.save(
                AuthDevice.builder()
                        .authDeviceName("Block A – Main OUT Reader")
                        .authDeviceDirection("E1_OUT")
                        .masterpin(false)
                        .controller(blockA)
                        .entrance(mainEntrance)
                        .lastOnline(LocalDateTime.now())
                        .build()
        );
        authDeviceRepository.save(
                AuthDevice.builder()
                        .authDeviceName("Block B – Side IN Reader")
                        .authDeviceDirection("E1_IN")
                        .masterpin(false)
                        .controller(blockB)
                        .entrance(sideEntrance)
                        .lastOnline(LocalDateTime.now().minusHours(2))
                        .build()
        );

        // ── Events (log entries with full context) ────────────────────────────
        eventRepository.save(Event.builder()
                .eventTime("04-16-2026 08:55:00.0").direction("IN")
                .eventActionType(typeAuthenticated).person(paulAtreides)
                .entrance(mainEntrance).accessGroup(dune).controller(blockA)
                .deleted(false).build());
        eventRepository.save(Event.builder()
                .eventTime("04-16-2026 09:10:00.0").direction("IN")
                .eventActionType(typeValidPin).person(letoAtreides)
                .entrance(mainEntrance).accessGroup(dune).controller(blockA)
                .deleted(false).build());
        eventRepository.save(Event.builder()
                .eventTime("04-16-2026 09:22:00.0").direction("IN")
                .eventActionType(typeAuthenticated).person(johnSmith)
                .entrance(mainEntrance).accessGroup(notDune).controller(blockA)
                .deleted(false).build());
        eventRepository.save(Event.builder()
                .eventTime("04-16-2026 10:05:00.0").direction("IN")
                .eventActionType(typeUnauthenticated)
                .entrance(sideEntrance).controller(blockB)
                .deleted(false).build());
        eventRepository.save(Event.builder()
                .eventTime("04-16-2026 12:30:00.0").direction("OUT")
                .eventActionType(typeAuthenticated).person(paulAtreides)
                .entrance(mainEntrance).accessGroup(dune).controller(blockA)
                .deleted(false).build());
        eventRepository.save(Event.builder()
                .eventTime("04-16-2026 13:45:00.0").direction("IN")
                .eventActionType(typeInvalidPin)
                .entrance(mainEntrance).controller(blockA)
                .deleted(false).build());
        eventRepository.save(Event.builder()
                .eventTime("04-16-2026 14:00:00.0").direction("IN")
                .eventActionType(typeDoorOpened).person(letoAtreides)
                .entrance(sideEntrance).accessGroup(dune).controller(blockB)
                .deleted(false).build());
        eventRepository.save(Event.builder()
                .eventTime("04-16-2026 17:30:00.0").direction("OUT")
                .eventActionType(typeAuthenticated).person(johnSmith)
                .entrance(mainEntrance).accessGroup(notDune).controller(blockA)
                .deleted(false).build());

        // ── Video Recorders ───────────────────────────────────────────────────
        videoRecorderRepository.save(new VideoRecorder(
                "Lobby NVR", "NVR-SN-001",
                "203.0.113.10", "192.168.1.200",
                8000, 80, "admin", "admin123", Boolean.FALSE));

        videoRecorderRepository.save(new VideoRecorder(
                "Carpark NVR", "NVR-SN-002",
                "203.0.113.11", "192.168.1.201",
                8000, 80, "admin", "admin123", Boolean.FALSE));

    }
}
