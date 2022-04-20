package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.entranceschedule.CreateEntranceScheduleDto;
import com.vmsac.vmsacserver.model.entranceschedule.EntranceSchedule;
import com.vmsac.vmsacserver.model.entranceschedule.EntranceScheduleDto;
import com.vmsac.vmsacserver.repository.EntranceRepository;
import com.vmsac.vmsacserver.repository.EntranceScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntranceScheduleService {

    @Autowired
    EntranceScheduleRepository entranceScheduleRepository;
    @Autowired
    EntranceRepository entranceRepository;

    public List<EntranceScheduleDto>findAll(){
        return entranceScheduleRepository.findAllByDeletedFalse().stream().map(EntranceSchedule::toDto).collect(Collectors.toList());
    }

    public List<EntranceScheduleDto>findAllByEntranceIdIn(List<Long> entranceIds){
        return entranceScheduleRepository.findAllByEntranceIdInAndDeletedFalse(entranceIds)
                .stream()
                .map(EntranceSchedule::toDto)
                .collect(Collectors.toList());
    }
    public List<EntranceScheduleDto>replaceSchedulesForEachId(List<CreateEntranceScheduleDto> createEntranceScheduleDtos,
                                                                    List<Long> entranceIds){
        if(entranceRepository.findByEntranceIdInAndDeletedFalse(entranceIds).size()!=entranceIds.size()){
            throw new RuntimeException("not all entrances exist");
        }
        List<EntranceSchedule> toDelete = entranceScheduleRepository.findAllByEntranceIdInAndDeletedFalse(entranceIds);
        toDelete.forEach(entranceSchedule -> entranceSchedule.setDeleted(true));
        entranceScheduleRepository.saveAll(toDelete);

        List<EntranceSchedule> toCreate = new ArrayList<>();
        for (CreateEntranceScheduleDto createEntranceScheduleDto:createEntranceScheduleDtos){
            toCreate.addAll(entranceIds.stream()
                    .map((id)->{
                        createEntranceScheduleDto.setEntranceId(id);
                        return createEntranceScheduleDto.toEntranceSchedule(false);
                    })
                    .collect(Collectors.toList()));

        }
        return entranceScheduleRepository.saveAll(toCreate).stream().map(EntranceSchedule::toDto).collect(Collectors.toList());
    }
    public List<EntranceScheduleDto> addSchedulesForEachId(List<CreateEntranceScheduleDto> createEntranceScheduleDtos,
                                                           List<Long> entranceIds){
        if(entranceRepository.findByEntranceIdInAndDeletedFalse(entranceIds).size()!=entranceIds.size()){
            throw new RuntimeException("not all entrances exist");
        }
        List<EntranceSchedule> toCreate = new ArrayList<>();
        for (CreateEntranceScheduleDto createEntranceScheduleDto:createEntranceScheduleDtos){
            toCreate.addAll(entranceIds.stream()
                    .map((id)->{
                        createEntranceScheduleDto.setEntranceId(id);
                        return createEntranceScheduleDto.toEntranceSchedule(false);
                    })
                    .collect(Collectors.toList()));

        }
        return entranceScheduleRepository.saveAll(toCreate).stream().map(EntranceSchedule::toDto).collect(Collectors.toList());
    }

    public void deleteScheduleWithId(Long scheduleId){
        EntranceSchedule toBeDeleted = entranceScheduleRepository.findByEntranceScheduleIdAndDeletedFalse(scheduleId)
                .orElseThrow(()->new RuntimeException("entrance schedule not found"));

        toBeDeleted.setDeleted(true);
        entranceScheduleRepository.save(toBeDeleted);
    }
}
