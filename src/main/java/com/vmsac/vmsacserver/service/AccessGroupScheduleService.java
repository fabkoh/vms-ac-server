package com.vmsac.vmsacserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.AccessGroupOnlyDto;
import com.vmsac.vmsacserver.model.Entrance;
import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoNDto;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupScheduleDto;
import com.vmsac.vmsacserver.model.accessgroupschedule.CreateAccessGroupScheduleDto;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceSchedule;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.AccessGroupScheduleRepository;
import com.vmsac.vmsacserver.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccessGroupScheduleService {
    @Autowired
    AccessGroupRepository accessGroupRepository;

    @Autowired
    AccessGroupScheduleRepository accessGroupScheduleRepository;

    @Autowired
    PersonRepository personRepository;
    @Autowired
    ControllerService controllerService;

    @Autowired
    private AccessGroupEntranceService accessGroupEntranceService;

    @Autowired
    AccessGroupEntranceNtoNRepository accessGroupEntranceRepository;

    //Get access group schedule by Id
    public Optional<AccessGroupSchedule> findById(Long Id){
        return accessGroupScheduleRepository.findByAccessGroupScheduleIdAndDeleted(Id,false);
    }

    //create an access group schedule
    public AccessGroupSchedule create(CreateAccessGroupScheduleDto createDto){
        return accessGroupScheduleRepository.save(createDto.toAccessGroupSchedule(false));
    }

    //save an access group schedule
    public AccessGroupSchedule save(AccessGroupScheduleDto accGrpSchedDto){
        return accessGroupScheduleRepository.save(accGrpSchedDto.toAccessGroupSchedule(false));
    }

    //delete access group schedule
    public AccessGroupSchedule delete(AccessGroupSchedule accGrpSched){
        return accessGroupScheduleRepository.save(accGrpSched);
    }

    // get schedule contents of createAccessGroupScheduleDto
    // and save it to an createAccessGroupSchedule with id from groupToEntranceIds
    public List<AccessGroupScheduleDto> createScheduleForEachId(CreateAccessGroupScheduleDto createAccessGroupScheduleDto,
                                                                List<Long> groupToEntranceIds) throws Exception {
        if (accessGroupEntranceRepository.findAllByGroupToEntranceIdInAndDeletedFalse(groupToEntranceIds).size() != groupToEntranceIds.size()) {
            // not all access group entrance exist
            throw new RuntimeException("not all access group are attached to entrance");
        }

        // create an AccessGroupSchedule with id in groupToEntranceIds
        List<AccessGroupSchedule> toSave = groupToEntranceIds.stream().map((id) -> {
            createAccessGroupScheduleDto.setGroupToEntranceId(id);
            return createAccessGroupScheduleDto.toAccessGroupSchedule(false);
        }).collect(Collectors.toList());

        // save all, then convert to dto
        return accessGroupScheduleRepository.saveAll(toSave).stream().map(AccessGroupSchedule::toDto).collect(Collectors.toList());
    }

    // remove schedules with groupToEntranceId in groupToEntranceIds
    // get schedule contents of createAccessGroupScheduleDtos
    // for each schedule: for each id: create a schedule
    public List<AccessGroupScheduleDto> replaceSchedulesForEachId(List<CreateAccessGroupScheduleDto> createAccessGroupScheduleDtos,
                                                                 List<Long> groupToEntranceIds) throws Exception {
        if (accessGroupEntranceRepository.findAllByGroupToEntranceIdInAndDeletedFalse(groupToEntranceIds).size() != groupToEntranceIds.size()) {
            // not all access group entrance exist
            throw new RuntimeException("not all access group are attached to entrance");
        }

        List<AccessGroupSchedule> toDelete = accessGroupScheduleRepository.findAllByGroupToEntranceIdInAndDeletedFalse(groupToEntranceIds);
        toDelete.forEach(accessGroupSchedule -> accessGroupSchedule.setDeleted(true));
        accessGroupScheduleRepository.saveAll(toDelete);

        List<AccessGroupSchedule> toCreate = new ArrayList<>();
        // for each createAccessGroupSchedule, for each id: add a createAccessGroupSchedule with groupToEntranceId id
        for (CreateAccessGroupScheduleDto createAccessGroupScheduleDto : createAccessGroupScheduleDtos) {
            toCreate.addAll(
                    groupToEntranceIds.stream()
                            .map((id) -> {
                                createAccessGroupScheduleDto.setGroupToEntranceId(id);
                                return createAccessGroupScheduleDto.toAccessGroupSchedule(false);
                            })
                            .collect(Collectors.toList())
            );
        }
        return accessGroupScheduleRepository.saveAll(toCreate).stream().map(AccessGroupSchedule::toDto).collect(Collectors.toList());
    }

    // get schedule contents in createAccessGroupScheduleDtos
    // for each schedule: for each id: create schedule
    public List<AccessGroupScheduleDto> addSchedulesForEachId(List<CreateAccessGroupScheduleDto> createAccessGroupScheduleDtos,
                                                               List<Long> groupToEntranceIds) throws Exception {
        if (accessGroupEntranceRepository.findAllByGroupToEntranceIdInAndDeletedFalse(groupToEntranceIds).size() != groupToEntranceIds.size()) {
            // not all access group entrance exist
            throw new RuntimeException("not all access group are attached to entrance");
        }

        List<AccessGroupSchedule> toCreate = new ArrayList<>();
        // for each createAccessGroupSchedule, for each id: add a createAccessGroupSchedule with groupToEntranceId id
        for (CreateAccessGroupScheduleDto createAccessGroupScheduleDto : createAccessGroupScheduleDtos) {
            toCreate.addAll(
                    groupToEntranceIds.stream()
                            .map((id) -> {
                                createAccessGroupScheduleDto.setGroupToEntranceId(id);
                                return createAccessGroupScheduleDto.toAccessGroupSchedule(false);
                            })
                            .collect(Collectors.toList())
            );
        }
        return accessGroupScheduleRepository.saveAll(toCreate).stream().map(AccessGroupSchedule::toDto).collect(Collectors.toList());
    }

    public void deleteScheduleWithId(Long scheduleId) {
        AccessGroupSchedule toBeDeleted =
                accessGroupScheduleRepository.findByAccessGroupScheduleIdAndDeletedFalse(scheduleId)
                        .orElseThrow(() -> new RuntimeException("Access group schedule not found"));

        toBeDeleted.setDeleted(true);
        accessGroupScheduleRepository.save(toBeDeleted);

        List<AccessGroupSchedule> accessGroupSchedules =
                accessGroupScheduleRepository.findAllByGroupToEntranceIdAndDeletedFalse(toBeDeleted.getGroupToEntranceId());

        if (accessGroupSchedules.size() == 0) { // last schedule
            AccessGroupEntranceNtoN groupEntrance =
                    accessGroupEntranceRepository.findByGroupToEntranceIdAndDeletedFalse(toBeDeleted.getGroupToEntranceId())
                            .orElseThrow(() -> new RuntimeException("Access group entrance n to n not found"));

            groupEntrance.setDeleted(true);
            accessGroupEntranceRepository.save(groupEntrance);
        }
    }

    public List<AccessGroupScheduleDto> findAll() {
        return accessGroupScheduleRepository.findAllByDeletedFalse().stream().map(AccessGroupSchedule::toDto).collect(Collectors.toList());
    }

    public List<AccessGroupScheduleDto> findAllByGroupToEntranceIdIn(List<Long> groupToEntranceIds) {
        return accessGroupScheduleRepository.findAllByGroupToEntranceIdInAndDeletedFalse(groupToEntranceIds)
                .stream()
                .map(AccessGroupSchedule::toDto)
                .collect(Collectors.toList());
    }

    public List<AccessGroupScheduleDto> findAllByGroupToEntranceIdInAndIsActiveTrue(List<Long> groupToEntranceIds) {
        return accessGroupScheduleRepository.findByGroupToEntranceIdInAndDeletedFalseAndIsActiveTrue(groupToEntranceIds)
                .stream()
                .map(AccessGroupSchedule::toDto)
                .collect(Collectors.toList());
    }

    public Map<Long, Boolean> GetAllAccessGroupCurrentStatus(){
        List <AccessGroup> ListOfAccessGroups = accessGroupRepository.findByDeleted(false);
        Map<Long, Boolean> Status = new HashMap();
        if (ListOfAccessGroups != null){
            for ( AccessGroup accessGroup  :  ListOfAccessGroups){
                Status.put(accessGroup.getAccessGroupId(), GetAccessGroupCurrentStatus(accessGroup.getAccessGroupId()));
            }
        }
        return Status;
    }

    public Boolean GetAllAccessGroupCurrentStatusForOnePerson(Long personId) {
        Person person = personRepository.findByPersonIdAndDeleted(personId,false).get();
        if(person != null) {
            AccessGroup accessGroup = person.getAccessGroup();
            return GetAccessGroupCurrentStatus(accessGroup.getAccessGroupId());
        }
        return false;
    }

    public Map<Long, Boolean> GetAllAccessGroupCurrentStatusForOneEntrance(Long entranceId){
        Map<Long, Boolean> Status = new HashMap();
        List<AccessGroup> ListOfAccessGroups = new ArrayList<>();
        List <AccessGroupEntranceNtoNDto> ListOfAccessGroupEntranceNtoNDto = accessGroupEntranceService.findAllWhereEntranceId(entranceId);

        for ( AccessGroupEntranceNtoNDto accessGroupEntranceNtoN  :  ListOfAccessGroupEntranceNtoNDto){
            AccessGroupOnlyDto accessGroupOnlyDto = accessGroupEntranceNtoN.getAccessGroup();
            AccessGroup accessGroup = accessGroupRepository.findByAccessGroupIdAndDeletedFalse(accessGroupOnlyDto.getAccessGroupId()).get();
            if (accessGroup != null) {
                ListOfAccessGroups.add(accessGroup);
            }
        }
        for ( AccessGroup accessGroup  :  ListOfAccessGroups){
            Status.put(accessGroup.getAccessGroupId(), GetAccessGroupCurrentStatus(accessGroup.getAccessGroupId()));
        }
        return Status;
    }

    // will return true if access group is currently being used in schedule and false otherwise
    public Boolean GetAccessGroupCurrentStatus(Long accessGroupId){
        AccessGroup existingAccessGroup = accessGroupRepository.findByAccessGroupIdAndDeletedFalse(accessGroupId).get();
        if (existingAccessGroup != null){
            Set<AccessGroupSchedule> accessGroupSchedules = new HashSet<>();
            List<AccessGroupEntranceNtoN> ListOfAccessGroupEntranceNtoN =accessGroupEntranceRepository.findAllByAccessGroupAccessGroupIdAndDeletedFalse(existingAccessGroup.getAccessGroupId());
            for ( AccessGroupEntranceNtoN accessGroupEntranceNtoN  :  ListOfAccessGroupEntranceNtoN){
                List<AccessGroupSchedule> accessGroupSchedule = accessGroupScheduleRepository.findAllByGroupToEntranceIdAndDeletedFalse(accessGroupEntranceNtoN.getGroupToEntranceId());
                for ( AccessGroupSchedule schedule  :  accessGroupSchedule){
                    accessGroupSchedules.add(schedule);
                }
            }
            List<AccessGroupSchedule> ListOfExistingAccessGroupSchedule = accessGroupSchedules.stream().collect(Collectors.toList());
            List<AccessGroupScheduleDto> ListOfExistingAccessGroupScheduleDto = new ArrayList<>();
            for (AccessGroupSchedule schedule : ListOfExistingAccessGroupSchedule) {
                ListOfExistingAccessGroupScheduleDto.add(schedule.toDto());
            }

            try{
                Map datetime = controllerService.GetAccessGroupScheduleObjectWithTime(ListOfExistingAccessGroupScheduleDto);
                DateTimeFormatter datef = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timef = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
//                System.out.println(datetime);
//                System.out.println(datef.format(now) + " " + timef.format(now));
//                System.out.println(datetime.containsKey(datef.format(now)));

                if ( datetime.containsKey(datef.format(now)) ) {
                    Object listOfTiming = datetime.get(datef.format(now));
//                    System.out.println(listOfTiming);
                    ObjectMapper listMapper = new ObjectMapper();
                    List listTiming = listMapper.convertValue(listOfTiming, List.class);
                    for ( Object eachTiming : listTiming){
//                        System.out.println("HERE");
//                        System.out.println(eachTiming);
                        ObjectMapper oMapper = new ObjectMapper();
                        Map<String, String> Sch = oMapper.convertValue(eachTiming, Map.class);
//                        System.out.println("TIMING");
                        String starttime = Sch.get("starttime");
                        String endtime = Sch.get("endtime");

                        LocalTime tEnd  = LocalTime.parse("23:59");
                        LocalTime tStart  = LocalTime.parse(starttime);
                        LocalTime tNow  = LocalTime.parse(timef.format(now));

                        if (!endtime.equals("24:00")){
                            tEnd  = LocalTime.parse(endtime);
                        }

                        if(tNow.compareTo(tStart)>=0 && tEnd.compareTo(tNow)>=0){
                            return true;
                        }
                    }
                }

            } catch (Exception e){
                System.out.println(e.toString());
                return false;
            }
        }

        return false;
    }

}
