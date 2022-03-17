package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupSchedule;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupScheduleDto;
import com.vmsac.vmsacserver.model.accessgroupschedule.CreateAccessGroupScheduleDto;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
import com.vmsac.vmsacserver.repository.AccessGroupScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccessGroupScheduleService {
    @Autowired
    AccessGroupScheduleRepository accessGroupScheduleRepository;

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


}
