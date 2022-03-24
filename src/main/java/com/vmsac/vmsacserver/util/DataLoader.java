package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.Entrance;
import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {
    private final AccessGroupRepository accessGroupRepository;
    private final EntranceRepository entranceRepository;
    private final PersonRepository personRepository;
    private final AccessGroupEntranceNtoNRepository accessGroupEntranceRepository;
    private final AccessGroupScheduleRepository accessGroupScheduleRepository;

    public DataLoader(AccessGroupRepository accessGroupRepository, EntranceRepository entranceRepository, PersonRepository personRepository, AccessGroupEntranceNtoNRepository accessGroupEntranceRepository, AccessGroupScheduleRepository accessGroupScheduleRepository) {
        this.accessGroupRepository = accessGroupRepository;
        this.entranceRepository = entranceRepository;
        this.personRepository = personRepository;
        this.accessGroupEntranceRepository = accessGroupEntranceRepository;
        this.accessGroupScheduleRepository = accessGroupScheduleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
       // loadData(); // uncomment to load data listed in loadData() below
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
                        .personMobileNumber("1 1001001000")
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
                        .personMobileNumber("+65 98765432")
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

        AccessGroupSchedule duneMainEntranceDefault = accessGroupScheduleRepository.save(
                AccessGroupSchedule.builder()
                        .accessGroupScheduleName("Default Schedule")
                        .rrule("FREQ=DAILY;INTERVAL=1;WKST=MO")
                        .timeStart("00:00")
                        .timeEnd("23:59")
                        .groupToEntranceId(duneMainEntrance.getGroupToEntranceId())
                        .deleted(false)
                        .build()
        );

        AccessGroupSchedule duneSideEntranceWeekdays = accessGroupScheduleRepository.save(
                AccessGroupSchedule.builder()
                        .accessGroupScheduleName("Weekdays 9 to 5")
                        .rrule("FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=MO,TU,WE,TH,FR")
                        .timeStart("09:00")
                        .timeEnd("17:00")
                        .groupToEntranceId(duneSideEntrance.getGroupToEntranceId())
                        .deleted(false)
                        .build()
        );

        AccessGroupSchedule duneSideEntranceWeekends = accessGroupScheduleRepository.save(
                AccessGroupSchedule.builder()
                        .accessGroupScheduleName("Weekends 12 to 2")
                        .rrule("FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=SA,SU")
                        .timeStart("12:00")
                        .timeEnd("17:00")
                        .groupToEntranceId(duneSideEntrance.getGroupToEntranceId())
                        .deleted(false)
                        .build()
        );

        AccessGroupSchedule notDuneMainEntranceDefault = accessGroupScheduleRepository.save(
                AccessGroupSchedule.builder()
                        .accessGroupScheduleName("Default Schedule")
                        .rrule("FREQ=DAILY;INTERVAL=1;WKST=MO")
                        .timeStart("00:00")
                        .timeEnd("23:59")
                        .groupToEntranceId(notDuneMainEntrance.getGroupToEntranceId())
                        .deleted(false)
                        .build()
        );
    }
}
