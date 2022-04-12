package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.Entrance;
import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;

import com.vmsac.vmsacserver.model.entranceschedule.EntranceSchedule;

import com.vmsac.vmsacserver.model.credential.Credential;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;

import com.vmsac.vmsacserver.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {
    private final AccessGroupRepository accessGroupRepository;
    private final EntranceRepository entranceRepository;
    private final PersonRepository personRepository;
    private final AccessGroupEntranceNtoNRepository accessGroupEntranceRepository;
    private final AccessGroupScheduleRepository accessGroupScheduleRepository;
    private final EntranceScheduleRepository entranceScheduleRepository;
    private final CredTypeRepository credTypeRepository;
    private final CredentialRepository credentialRepository;


    public DataLoader(AccessGroupRepository accessGroupRepository, EntranceRepository entranceRepository, PersonRepository personRepository, AccessGroupEntranceNtoNRepository accessGroupEntranceRepository, AccessGroupScheduleRepository accessGroupScheduleRepository, CredTypeRepository credTypeRepository, CredentialRepository credentialRepository, EntranceScheduleRepository entranceScheduleRepository) {

        this.accessGroupRepository = accessGroupRepository;
        this.entranceRepository = entranceRepository;
        this.personRepository = personRepository;
        this.accessGroupEntranceRepository = accessGroupEntranceRepository;
        this.accessGroupScheduleRepository = accessGroupScheduleRepository;

        this.entranceScheduleRepository = entranceScheduleRepository;

        this.credTypeRepository = credTypeRepository;
        this.credentialRepository = credentialRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadData(); // uncomment to load data listed in loadData() below
    }

    private void loadData() {
        AccessGroup dune = accessGroupRepository.save(
                AccessGroup.builder()
                        .accessGroupName("Dune")
                        .accessGroupDesc("the characters from dune")
                        .deleted(false)
                        .build()
        );

        AccessGroup notDune = accessGroupRepository.save(
                AccessGroup.builder()
                        .accessGroupName("Not dune")
                        .deleted(false)
                        .build()
        );

        AccessGroup emptyGroup = accessGroupRepository.save(
                AccessGroup.builder()
                        .accessGroupName("Empty group")
                        .deleted(false)
                        .build()
        );

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
                        .build()
        );

        Entrance sideEntrance = entranceRepository.save(
                Entrance.builder()
                        .entranceName("Side Entrance")
                        .isActive(true)
                        .deleted(false)
                        .build()
        );

        Entrance abandonedEntrance = entranceRepository.save(
                Entrance.builder()
                        .entranceName("Abandoned Entrance")
                        .isActive(false)
                        .deleted(false)
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
        String rruleDtstart = "DTSTART:" + dtf.format(LocalDateTime.now()) + "T000000Z\nRRULE:";

        AccessGroupSchedule duneMainEntranceDefault = accessGroupScheduleRepository.save(
                AccessGroupSchedule.builder()
                        .accessGroupScheduleName("Default Schedule")
                        .rrule(rruleDtstart + "FREQ=DAILY;INTERVAL=1;WKST=MO")
                        .timeStart("00:00")
                        .timeEnd("23:59")
                        .groupToEntranceId(duneMainEntrance.getGroupToEntranceId())
                        .deleted(false)
                        .build()
        );

        AccessGroupSchedule duneSideEntranceWeekdays = accessGroupScheduleRepository.save(
                AccessGroupSchedule.builder()
                        .accessGroupScheduleName("Weekdays 9 to 5")
                        .rrule(rruleDtstart + "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=MO,TU,WE,TH,FR")
                        .timeStart("09:00")
                        .timeEnd("17:00")
                        .groupToEntranceId(duneSideEntrance.getGroupToEntranceId())
                        .deleted(false)
                        .build()
        );

        AccessGroupSchedule duneSideEntranceWeekends = accessGroupScheduleRepository.save(
                AccessGroupSchedule.builder()
                        .accessGroupScheduleName("Weekends 12 to 2")
                        .rrule(rruleDtstart + "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=SA,SU")
                        .timeStart("12:00")
                        .timeEnd("17:00")
                        .groupToEntranceId(duneSideEntrance.getGroupToEntranceId())
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
                        .build()
        );

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
    }
}
