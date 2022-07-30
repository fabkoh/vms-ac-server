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
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Autowired
    EntityManager em;

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

    @Transactional
    public AuthDevice AuthDeviceEntranceUpdate(AuthDevice newAuthDevice,Entrance entrance) throws IllegalArgumentException{
        AuthDevice exisitingAuthDevice = authDeviceRepository.findById(newAuthDevice.getAuthDeviceId())
                .orElseThrow(()-> new RuntimeException("Auth Device does not exist"));

        System.out.println("---" + newAuthDevice.getAuthDeviceName());
        // get all controller EvM (includes those of the assigned entrances)
        Controller c = exisitingAuthDevice.getController();
        Set<EventsManagement> controllerEvm = c.getAllEventsManagement();
        System.out.println("Length of EvMs is " + controllerEvm.size());

        if (entrance != null) {
            List<EventsManagement> entranceEvm = entrance.getEventsManagements();

            controllerEvm.forEach(em1 -> {
                // check input events
                System.out.println("---" + inputEventRepo.findAllById(em1.getInputEventsId()));
                inputEventRepo.findAllById(em1.getInputEventsId()).forEach(ie -> {
                    String name1 = ie.getEventActionInputType().getEventActionInputName();
                    if (name1.startsWith("GEN_IN_")) {
                        entranceEvm.forEach(em2 -> {
                            outputEventRepo.findAllById(em2.getOutputActionsId()).forEach(oe -> {
                                String name2 = oe.getEventActionOutputType().getEventActionOutputName();
                                if (name2.equals("GEN_OUT_" + name1.substring(7)))
                                    throw new IllegalArgumentException(name1 + " " + name2);
                            });
                        });
                    }
                });

                // check output events
                outputEventRepo.findAllById(em1.getOutputActionsId()).forEach(oe -> {
                    String name1 = oe.getEventActionOutputType().getEventActionOutputName();
                    if (name1.startsWith("GEN_OUT_")) {
                        entranceEvm.forEach(em2 -> {
                            inputEventRepo.findAllById(em2.getInputEventsId()).forEach(ie -> {
                                String name2 = ie.getEventActionInputType().getEventActionInputName();
                                if (name2.equals("GEN_IN_" + name1.substring(8)))
                                    throw new IllegalArgumentException(name1 + " " + name2);
                            });
                        });
                    }
                });
            });
        }

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
