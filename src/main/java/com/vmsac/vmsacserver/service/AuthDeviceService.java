package com.vmsac.vmsacserver.service;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import com.vmsac.vmsacserver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.naming.ConfigurationException;
import javax.naming.InvalidNameException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AuthDeviceService {



    @Autowired
    private AuthMethodRepository authMethodRepository;

    @Autowired
    private AuthDeviceRepository authDeviceRepository;

    @Autowired
    private ControllerService controllerService;

    @Autowired
    private EntranceService entranceService;

    @Autowired
    private AuthMethodScheduleRepository authMethodScheduleRepository;

    @Autowired
    private InputEventRepository inputEventRepo;

    @Autowired
    private OutputEventRepository outputEventRepo;

    @Autowired
    private GENConfigsRepository genRepo;

    long defaultAuthMethodId = 9L;

    public void createAuthDevices(Controller controller) throws Exception{
        AuthMethod defaultAuthMethod = authMethodRepository.findById(defaultAuthMethodId).get();

        AuthDevice authdevice1 = new AuthDevice();
        authDeviceRepository.save(authdevice1.toCreateAuthDevice("Auth Device E1_IN", "E1_IN",
                defaultAuthMethod,controller));

        AuthDevice authdevice2 = new AuthDevice();
        authDeviceRepository.save(authdevice2.toCreateAuthDevice("Auth Device E1_OUT", "E1_OUT",
                defaultAuthMethod,controller));

        AuthDevice authdevice3 = new AuthDevice();
        authDeviceRepository.save(authdevice3.toCreateAuthDevice("Auth Device E2_IN", "E2_IN",
                defaultAuthMethod,controller));

        AuthDevice authdevice4 = new AuthDevice();
        authDeviceRepository.save(authdevice4.toCreateAuthDevice("Auth Device E2_OUT", "E2_OUT",
                defaultAuthMethod,controller));
    }

    public AuthDevice resetAuthDevice(Long authdeviceid) throws Exception{
        AuthMethod defaultAuthMethod = authMethodRepository.findById(defaultAuthMethodId).get();

        AuthDevice existingAuthDevice = authDeviceRepository.findById(authdeviceid)
                .orElseThrow(() -> new RuntimeException("Auth Device with id "+ authdeviceid+ " does not exist"));

        existingAuthDevice.setAuthDeviceName("Auth Device "+existingAuthDevice.getAuthDeviceDirection());
        existingAuthDevice.setLastOnline(null);
        existingAuthDevice.setMasterpin(Boolean.FALSE);
        existingAuthDevice.setDefaultAuthMethod(defaultAuthMethod);
        //set authMethodSChedules to false
        List<AuthMethodSchedule> toDeleteSched = authMethodScheduleRepository.findByAuthDevice_AuthDeviceIdAndDeletedFalse(authdeviceid);
        toDeleteSched.forEach(authMethodSchedule -> authMethodSchedule.setDeleted(true));
        authMethodScheduleRepository.saveAll(toDeleteSched);

        return authDeviceRepository.save(existingAuthDevice);

    }

    public AuthDevice deleteAuthDevice(Long authdeviceid) throws Exception{
        AuthMethod defaultAuthMethod = authMethodRepository.findById(defaultAuthMethodId).get();
        AuthDevice existingAuthDevice = authDeviceRepository.findById(authdeviceid)
                .orElseThrow(() -> new RuntimeException("Auth Device with id "+ authdeviceid+ " does not exist"));

        existingAuthDevice.setAuthDeviceName("Auth Device "+existingAuthDevice.getAuthDeviceDirection());
        existingAuthDevice.setMasterpin(Boolean.FALSE);
        existingAuthDevice.setDefaultAuthMethod(defaultAuthMethod);
        //set authMethodSChedules to false
        List<AuthMethodSchedule> toDeleteSched = authMethodScheduleRepository.findByAuthDevice_AuthDeviceIdAndDeletedFalse(authdeviceid);
        toDeleteSched.forEach(authMethodSchedule -> authMethodSchedule.setDeleted(true));
        authMethodScheduleRepository.saveAll(toDeleteSched);

        return authDeviceRepository.save(existingAuthDevice);

    }

    @Transactional
    public void deleteRelatedAuthDevices(Long controllerId) throws Exception {
        entranceService.FreeEntrances(controllerId);
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
        exisitingAuthDevice.setDefaultAuthMethod(authMethodRepository.findById(newAuthDevice.getDefaultAuthMethod().getAuthMethodId().longValue()).get());
        return authDeviceRepository.save(exisitingAuthDevice);
    }

    public AuthDevice AuthDeviceEntranceUpdate(AuthDevice newAuthDevice,Entrance entrance) throws IllegalArgumentException{
        AuthDevice exisitingAuthDevice = authDeviceRepository.findById(newAuthDevice.getAuthDeviceId())
                .orElseThrow(()-> new RuntimeException("Auth Device does not exist"));

        Controller c = exisitingAuthDevice.getController();

        List<EventsManagement> ems = entrance.getEventsManagements();

        ems.forEach(em -> {
            // check input events
            List<InputEvent> ies = inputEventRepo.findAllById(em.getInputEventsId());
            ies.forEach(ie -> {
                String name = ie.getEventActionInputType().getEventActionInputName();
                if (name.startsWith("GEN_IN")) {
                    GENConfigs g = genRepo.getByController_ControllerIdAndPinName(c.getControllerId(), name.substring(7));
                    if (g.getStatus() != null && g.getStatus().equals("OUT"))
                        throw new IllegalArgumentException(g.getOutName());
                    else {
                        g.setStatus("IN");
                        genRepo.save(g);
                    }
                }
            });

            // check output events
            List<OutputEvent> oes = outputEventRepo.findAllById(em.getOutputActionsId());
            oes.forEach(oe -> {
                String name = oe.getEventActionOutputType().getEventActionOutputName();
                if (name.startsWith("GEN_OUT")) {
                    GENConfigs g = genRepo.getByController_ControllerIdAndPinName(c.getControllerId(), name.substring(8));
                    if (g.getStatus() != null && g.getStatus().equals("IN"))
                        throw new IllegalArgumentException(g.getInName());
                    else {
                        g.setStatus("OUT");
                        genRepo.save(g);
                    }
                }
            });
        });

        exisitingAuthDevice.setEntrance(entrance);
        return authDeviceRepository.save(exisitingAuthDevice);
    }

    public List<AuthDevice> findbyEntranceid(Long entranceid){
        return authDeviceRepository.findByEntrance_EntranceIdEquals(entranceid);
    }

    public List<AuthDevice> findbyControllerId(Long controllerId){
        return authDeviceRepository.findByController_ControllerIdEquals(controllerId);
    }

    public void UpdateAuthDeviceMasterpin(Long authdeviceId, Boolean state)throws Exception{
        AuthDevice exisitingAuthDevice = authDeviceRepository.findById(authdeviceId)
                .orElseThrow(()-> new RuntimeException("Auth Device does not exist"));

        exisitingAuthDevice.setMasterpin(state);
        authDeviceRepository.save(exisitingAuthDevice);

    }

    public void save(AuthDevice existingauthDevice) {
        authDeviceRepository.save(existingauthDevice);
    }
}
