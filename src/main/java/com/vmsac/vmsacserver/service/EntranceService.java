package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.EntranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntranceService {

    @Autowired
    AccessGroupRepository AccessGroupRepository;

    @Autowired
    EntranceRepository entranceRepository;

    @Autowired
    AccessGroupEntranceService accessGroupEntranceService;

    @Autowired
    AccessGroupEntranceNtoNRepository accessGroupEntranceNtoNRepository;

    @Autowired
    AuthDeviceService authDeviceService;

    //read methods
    //returns all undeleted entrances
    public List<EntranceDto> findAllEntrances(){
        return entranceRepository.findByDeleted(false).stream()
                .map(Entrance::toDto)
                .collect(Collectors.toList());
    }
    public Boolean nameInUse(String name){
        return entranceRepository.findByEntranceNameAndDeletedFalse(name).isPresent();
    }

    //returns queried entrance
    public Optional<Entrance> findById(Long Id){
        return entranceRepository.findByEntranceIdAndDeletedFalse(Id);
    }

    //create entrance
    public EntranceDto createEntrance(CreateEntranceDto EntranceDto){
        return entranceRepository.save(EntranceDto.toEntrance(false)).toDto();
    }

    //update entrance
    public Entrance save(EntranceDto entranceDto){
        return entranceRepository.save(entranceDto.toEntrance(false));
    }

    // set isActive to true for the given entrance ids
    public EntranceDto updateEntranceIsActiveWithId(Boolean isActive, Long entranceId) throws Exception {
        Entrance entrance = entranceRepository.findByEntranceIdAndDeletedFalse(entranceId).orElseThrow();
        entrance.setIsActive(isActive);
        return entranceRepository.save(entrance).toDto();
    }
    //delete entrance and its groupToEntrance relationships
    public void delete(Long Id){
        Entrance deleted = entranceRepository.findByEntranceIdAndDeletedFalse(Id).get();
        deleted.setDeleted(true);
        deleted.setUsed(false);
        List <AuthDevice> authdevicelist = authDeviceService.findbyEntranceid(Id);
        authdevicelist.forEach(authdevice-> {
            try {
                authDeviceService.AuthDeviceEntranceUpdate(authdevice,null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });



        entranceRepository.save(deleted);

        List<AccessGroupEntranceNtoN> toDelete = accessGroupEntranceNtoNRepository.findAllByEntranceEntranceIdAndDeletedFalse(Id);
        toDelete.stream()
                .forEach(accessGroupEntranceNtoN -> accessGroupEntranceNtoN.setDeleted(true));
        accessGroupEntranceService.deleteAccessGroupEntranceNtoN(toDelete);
    }

    public List <Entrance> getAvailableEntrances(){
        return entranceRepository.findByDeletedIsFalseAndUsedIsFalse();
    }

    public void setEntranceUsed(Entrance entrance,Boolean status)throws Exception{

        if (entrance != null) {
            Entrance existingEntrance = findById(entrance.getEntranceId()).get();
            existingEntrance.setUsed(status);
            entranceRepository.save(existingEntrance);
        }
    }
}
