package com.vmsac.vmsacserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmsac.vmsacserver.model.Entrance;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.CreateEntranceScheduleDto;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceSchedule;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceScheduleDto;
import com.vmsac.vmsacserver.repository.EntranceRepository;
import com.vmsac.vmsacserver.repository.EntranceScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class EntranceScheduleService {

    @Autowired
    EntranceScheduleRepository entranceScheduleRepository;
    @Autowired
    EntranceRepository entranceRepository;
    @Autowired
    ControllerService controllerService;

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

    public Map GetAllEntrancesCurrentStatus(){
        List <Entrance> ListOfEntrance = entranceRepository.findByDeletedIsFalse();
        Map Status = new HashMap();
        if (ListOfEntrance != null){
            for ( Entrance singleEntrance  :  ListOfEntrance){
                Status.put(singleEntrance.getEntranceId(), GetEntranceCurrentStatus(singleEntrance.getEntranceId()));
            }
        }
        return Status;
    }

    public Boolean GetEntranceCurrentStatus(Long entranceId){
        Entrance existingEntrance = entranceRepository.findByEntranceIdAndDeletedFalse(entranceId).get();
        if (existingEntrance != null){
            List<EntranceSchedule> ListOfExistingEntranceSch = entranceScheduleRepository.findAllByEntranceIdAndDeletedFalse(entranceId);
            try{
                Map datetime = controllerService.GetEntranceScheduleObjectWithTime(ListOfExistingEntranceSch);
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
