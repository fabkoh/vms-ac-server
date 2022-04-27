package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AuthDevice;
import com.vmsac.vmsacserver.model.Controller;
import com.vmsac.vmsacserver.model.Entrance;
import com.vmsac.vmsacserver.repository.AuthDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AuthDeviceService {

    String defaultAuthMethod = "CardAndPin";

    @Autowired
    private AuthDeviceRepository authDeviceRepository;

    @Autowired
    private ControllerService controllerService;

    public void createAuthDevices(Controller controller) throws Exception{
        AuthDevice authdevice1 = new AuthDevice();
        authDeviceRepository.save(authdevice1.toCreateAuthDevice("Auth Device 1", "E1 IN",
                defaultAuthMethod,controller));

        AuthDevice authdevice2 = new AuthDevice();
        authDeviceRepository.save(authdevice2.toCreateAuthDevice("Auth Device 2", "E1 OUT",
                defaultAuthMethod,controller));

        AuthDevice authdevice3 = new AuthDevice();
        authDeviceRepository.save(authdevice3.toCreateAuthDevice("Auth Device 3", "E2 IN",
                defaultAuthMethod,controller));

        AuthDevice authdevice4 = new AuthDevice();
        authDeviceRepository.save(authdevice4.toCreateAuthDevice("Auth Device 4", "E2 OUT",
                defaultAuthMethod,controller));
    }

    public AuthDevice resetAuthDevice(Long authdeviceid) throws Exception{
        AuthDevice authdevice1 = new AuthDevice();
        AuthDevice existingAuthDevice = authDeviceRepository.findById(authdeviceid)
                .orElseThrow(() -> new RuntimeException("Auth Device with id "+ authdeviceid+ " does not exist"));

        existingAuthDevice.setAuthDeviceName(existingAuthDevice.getAuthDeviceDirection());
        existingAuthDevice.setLastOnline(null);
        existingAuthDevice.setMasterpin(Boolean.TRUE);
        existingAuthDevice.setDefaultAuthMethod("CardAndPin");

        return authDeviceRepository.save(existingAuthDevice);

    }

    @Transactional
    public void deleteRelatedAuthDevices(Long controllerId){
        authDeviceRepository.deleteByController_ControllerIdEquals(controllerId);

    }

    public Optional <AuthDevice> findbyId(Long authdeviceid){
        return authDeviceRepository.findById(authdeviceid);

    }

    public AuthDevice AuthDeviceUpdate(AuthDevice newAuthDevice) throws Exception{
        AuthDevice exisitingAuthDevice = authDeviceRepository.findById(newAuthDevice.getAuthDeviceId())
                .orElseThrow(()-> new RuntimeException("Auth Device does not exist"));

        exisitingAuthDevice.setAuthDeviceName(newAuthDevice.getAuthDeviceName());
        exisitingAuthDevice.setMasterpin(newAuthDevice.getMasterpin());
        exisitingAuthDevice.setDefaultAuthMethod(newAuthDevice.getDefaultAuthMethod());
        return authDeviceRepository.save(exisitingAuthDevice);
    }

    public AuthDevice AuthDeviceEntranceUpdate(AuthDevice newAuthDevice,Entrance entrance) throws Exception{
        AuthDevice exisitingAuthDevice = authDeviceRepository.findById(newAuthDevice.getAuthDeviceId())
                .orElseThrow(()-> new RuntimeException("Auth Device does not exist"));

        exisitingAuthDevice.setEntrance(entrance);
        return authDeviceRepository.save(exisitingAuthDevice);
    }

    public List<AuthDevice> findbyEntranceid(Long entranceid){
        return authDeviceRepository.findByEntrance_EntranceIdEquals(entranceid);
    }

    public void UpdateAuthDeviceMasterpin(Long authdeviceId, Boolean state)throws Exception{
        AuthDevice exisitingAuthDevice = authDeviceRepository.findById(authdeviceId)
                .orElseThrow(()-> new RuntimeException("Auth Device does not exist"));

        exisitingAuthDevice.setMasterpin(state);
        authDeviceRepository.save(exisitingAuthDevice);

    }
}
