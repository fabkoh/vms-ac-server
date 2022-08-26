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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

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

    long defaultAuthMethodId = 1L;

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

        // get all controller EvM (includes those of the assigned entrances)
        Controller c = exisitingAuthDevice.getController();
        Set<EventsManagement> controllerEvm = c.getAllEventsManagement();

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

    public String findCurrentAuthMethod(Long authdeviceId){
        List<AuthMethodSchedule> listOfExistingAuthMethodSchedules = authMethodScheduleRepository.findByAuthDevice_AuthDeviceIdAndDeletedFalse(authdeviceId);

        for ( AuthMethodSchedule authMethodSchedule :  listOfExistingAuthMethodSchedules){

            String rawrrule = authMethodSchedule.getRrule();
            String starttime = authMethodSchedule.getTimeStart();
            String endtime = authMethodSchedule.getTimeEnd();


            String startdatetime = rawrrule.split("\n")[0].split(":")[1].split("T")[0];
            String rrule = rawrrule.split("\n")[1].split(":")[1];

            System.out.println(startdatetime +" "+ rrule +" "+ starttime +" "+ endtime);

            // startdatetime will always be valid
            String today = String.valueOf(LocalDate.now().getDayOfWeek()).substring(0,2);
            String byDay =  rrule.split("BYDAY=")[1];
            System.out.println(today +":" + byDay);
            //SU,MO,TU,WE,TH,FR,SA
            if (byDay.contains(today)){
                // check for timing
                LocalTime timeNow = LocalTime.now(ZoneId.of("GMT+08:00"));

                LocalTime startTime = LocalTime.parse(starttime);

                if (endtime.equals("24:00")) {
                    if( timeNow.compareTo(startTime)>=0){
                        return authMethodSchedule.getAuthMethod().getAuthMethodDesc();
                    }
                }

                LocalTime endTime = LocalTime.parse(endtime);


                if( (timeNow.compareTo(startTime)>=0) && (endTime.compareTo(timeNow)>=0)){
                    return authMethodSchedule.getAuthMethod().getAuthMethodDesc();
                }
            }

        };

        return authDeviceRepository.findById(authdeviceId).get().getDefaultAuthMethod().getAuthMethodDesc();
    }

    public Map<String, String> findControllerCurrentAuthMethod(Long controllerId){
        Map<String, String> listOfCurrentAuthMethod = new HashMap<>();
        List <AuthDevice> ListOfAuthDevice = authDeviceRepository.findByController_ControllerIdEquals(controllerId);
        for ( AuthDevice authDevice  : ListOfAuthDevice){
            listOfCurrentAuthMethod.put(authDevice.getAuthDeviceId().toString(),findCurrentAuthMethod(authDevice.getAuthDeviceId()));
        }
        return listOfCurrentAuthMethod;
    }

    public void UpdateAuthDeviceMasterpin(Long authdeviceId, Boolean state)throws Exception{
        AuthDevice exisitingAuthDevice = authDeviceRepository.findById(authdeviceId)
                .orElseThrow(()-> new RuntimeException("Auth Device does not exist"));

        exisitingAuthDevice.setMasterpin(state);
        authDeviceRepository.save(exisitingAuthDevice);

    }

    public List<AuthDevice> findbyControllerId(Long controllerId){
        return authDeviceRepository.findByController_ControllerIdEquals(controllerId);
    }

    public void save(AuthDevice existingauthDevice) {
        authDeviceRepository.save(existingauthDevice);
    }
}
