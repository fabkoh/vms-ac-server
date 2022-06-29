package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
import com.vmsac.vmsacserver.repository.AuthDeviceRepository;
import com.vmsac.vmsacserver.repository.AuthMethodRepository;
import com.vmsac.vmsacserver.repository.ControllerRepository;
import com.vmsac.vmsacserver.service.AuthDeviceService;
import com.vmsac.vmsacserver.service.ControllerService;
import com.vmsac.vmsacserver.service.EntranceService;

import com.vmsac.vmsacserver.util.UniconUpdater;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@EnableAsync
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ControllerController {

    @Autowired
    private UniconUpdater uniconUpdater;

    @Autowired
    private ControllerService controllerService;

    @Autowired
    private AuthDeviceService authDeviceService;

    @Autowired
    private EntranceService entranceService;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private ControllerRepository controllerRepository;

    @Autowired
    private AuthMethodRepository authMethodRepository;

    @GetMapping("/controllers")
    public List<Controller> getcontrollers() {
        return controllerService.findAllNotDeleted();
    }

    @GetMapping("/controller/{controllerId}")
    public ResponseEntity<?> getController(@PathVariable Long controllerId) {


        Optional<Controller> optionalController = controllerService.findById(controllerId);
        if (optionalController.isPresent()) {
            return ResponseEntity.ok(optionalController.get());
        }

        Map<String, String> errors = new HashMap<>();
        errors.put("controllerId", "Controller with Id " +
                controllerId + " does not exist");

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }


    @PutMapping(path = "controller/{controllerId}")
    public ResponseEntity<?> UpdateController( @PathVariable Long controllerId,
            @Valid @RequestBody FrontendControllerDto newFrontendControllerDto) throws Exception {
        Optional<Controller> optionalController = controllerService.findBySerialNo(newFrontendControllerDto.getControllerSerialNo());

        if (optionalController.isPresent() && optionalController.get().getControllerId() == controllerId) {

            if (optionalController.get().getControllerIP().equals(newFrontendControllerDto.getControllerIP())){
                return new ResponseEntity<>(controllerService.FrondEndControllerUpdate(newFrontendControllerDto), HttpStatus.OK);
            }

            if (optionalController.get().getMasterController() == true){
                Map<String, String> errors = new HashMap<>();
                errors.put("controllerId", "Controller with Id " +
                        controllerId + " with Serial No " + newFrontendControllerDto.getControllerSerialNo()+" is a Master Controller and cannot be edited.");
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            if (controllerService.existsByControllerNameEquals(newFrontendControllerDto.getControllerName())){
                Map<String, String> errors = new HashMap<>();
                errors.put("controllerNameError", "Controller with name " + newFrontendControllerDto.getControllerName()+" already exists.");
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            if (newFrontendControllerDto.getControllerIPStatic() == false){

                try{
                    if (optionalController.get().getControllerIPStatic() == true){
                        controllerService.FrondEndControllerUpdate(newFrontendControllerDto);
                        if (controllerService.UpdateUniconIP(newFrontendControllerDto)) {
                            return new ResponseEntity<>(HttpStatus.OK);
                        }

                        Map<String, String> errors = new HashMap<>();
                        errors.put("Error", "Unexpected error in updateunicon" );
                        return new ResponseEntity<>(errors, HttpStatus.GONE);
                    }
                    else{
                        return new ResponseEntity<>(controllerService.FrondEndControllerUpdate(newFrontendControllerDto), HttpStatus.OK);
                    }

                }
                catch(Exception e){
                    Map<String, String> errors = new HashMap<>();
                    errors.put("Error", "CONTROLLER MIGHT BE LOST ! Fail to update Controller with ID "+ newFrontendControllerDto.getControllerId() );
                    return new ResponseEntity<>(errors, HttpStatus.GONE);
                }


            }

            if (optionalController.get().getPendingIP() != optionalController.get().getControllerIP() && optionalController.get().getPendingIP() != null ){
                Map<String, String> errors = new HashMap<>();
                errors.put("controllerId", "Controller with Id " +
                        controllerId + " with Serial No " + newFrontendControllerDto.getControllerSerialNo()+" has clashing pending and current IP ");
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            if (controllerService.isNotValidInet4Address(newFrontendControllerDto.getControllerIP())){
                Map<String, String> errors = new HashMap<>();
                errors.put("Error", newFrontendControllerDto.getControllerIP() +" is not a valid IPv4 address");
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            // check if mastercontroller, return error if true
            // check if pendingIP != actualIP && pendingIP != null, return error
            try{
                if (!(newFrontendControllerDto.getControllerIP().equals(optionalController.get().getControllerIP())) && controllerService.IsIPavailable(newFrontendControllerDto.getControllerIP())){
                    // ping ip to see if taken ( return error if taken )
                    // update pending ip in db

                    if (controllerService.UpdateUniconIP(newFrontendControllerDto)){
                        return new ResponseEntity<>(controllerService.FrondEndControllerUpdate(newFrontendControllerDto), HttpStatus.OK);
                    }

                    Map<String, String> errors = new HashMap<>();
                    errors.put("Error", "CONTROLLER MIGHT BE LOST ! Fail to update Controller with ID "+ newFrontendControllerDto.getControllerId() );
                    return new ResponseEntity<>(errors, HttpStatus.GONE);

                }

                Map<String, String> errors = new HashMap<>();
                errors.put("Error", newFrontendControllerDto.getControllerIP() +" is taken");
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            catch (Exception e){
                return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
            }
            //////////////////// check if ip address is in same subnet

            // send req to pi and wait for response
            // time out, start sending POST req for healthcheck to pi new address
                // ( after a {certain time}, return error )
                // replies within a {certain time},
                    // update name and actual ip in db and return success
            // if pi replies, return error

            //return Response 200
        }
        Map<String, String> errors = new HashMap<>();
        errors.put("controllerId", "Controller with Id " +
                controllerId + " with Serial No " + newFrontendControllerDto.getControllerSerialNo()+" does not exist");

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @PostMapping(path = "unicon/controller")
    public ResponseEntity<?> createOrUpdateController(
            @Valid @RequestBody UniconControllerDto newUniconControllerDto) {
        Optional<Controller> optionalController = controllerService.findBySerialNo(newUniconControllerDto.getControllerSerialNo());

        if (optionalController.isPresent()) {
            try {
                return new ResponseEntity<>(controllerService.uniconControllerUpdate(newUniconControllerDto), HttpStatus.OK);
            }//update
            catch(Exception e){
                return ResponseEntity.badRequest().build();
            }
            //return Response 200
        } else {
//        if serial no does not exist or exist + deleted : true, create
            // return Response 201
            UniconControllerDto created = controllerService.uniconControllerCreate(newUniconControllerDto);
            try {
                authDeviceService.createAuthDevices(created.toController());
                getControllerConnection(created.getControllerId());
            }
            catch(Exception e)
            {
                return ResponseEntity.badRequest().build();
            }

            return new ResponseEntity<>(created, HttpStatus.CREATED);

        }
    }

    @GetMapping(path = "authdevice/{authdeviceId}")
    public ResponseEntity<?> GetAuthDevice( @PathVariable Long authdeviceId) {

        if (authDeviceService.findbyId(authdeviceId).isPresent()){
            return new ResponseEntity<>(authDeviceService.findbyId(authdeviceId), HttpStatus.OK);
        }
        Map<String, String> errors = new HashMap<>();
        errors.put("authdeviceId", "Auth Device with Id " +
                authdeviceId + " does not exist");

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @PutMapping(path = "authdevice/masterpinToTrue/{authdeviceId}")
    public ResponseEntity<?> UpdateAuthDeviceMasterpinToTrue( @PathVariable Long authdeviceId) {
        Optional<AuthDevice> optionalAuthDevice = authDeviceService.findbyId(authdeviceId);

        if (optionalAuthDevice.isPresent()){
            try {
                if (!optionalAuthDevice.get().getMasterpin()){
                    authDeviceService.UpdateAuthDeviceMasterpin(authdeviceId,true);

                    return new ResponseEntity<>(HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }//update
            catch(Exception e){
                return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
            }
            //return Response 200
        }
        Map<String, String> errors = new HashMap<>();
        errors.put("authdeviceId", "Auth Device with Id " +
                authdeviceId +" does not exist");

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @PutMapping(path = "authdevice/masterpinToFalse/{authdeviceId}")
    public ResponseEntity<?> UpdateAuthDeviceMasterpinToFalse( @PathVariable Long authdeviceId) {
        Optional<AuthDevice> optionalAuthDevice = authDeviceService.findbyId(authdeviceId);

        if (optionalAuthDevice.isPresent()){
            try {
                if (optionalAuthDevice.get().getMasterpin()){
                    authDeviceService.UpdateAuthDeviceMasterpin(authdeviceId,false);

                    return new ResponseEntity<>(HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }//update
            catch(Exception e){
                return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
            }
            //return Response 200
        }
        Map<String, String> errors = new HashMap<>();
        errors.put("authdeviceId", "Auth Device with Id " +
                authdeviceId +" does not exist");

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @PutMapping(path = "authdevice/{authdeviceId}")
    public ResponseEntity<?> UpdateAuthDevice( @PathVariable Long authdeviceId,
                                               @Valid @RequestBody AuthDevice newAuthDevice) {
        Optional<AuthDevice> optionalAuthDevice = authDeviceService.findbyId(newAuthDevice.getAuthDeviceId());

        if (optionalAuthDevice.isPresent() && optionalAuthDevice.get().getAuthDeviceId() == authdeviceId) {
            try {
                authDeviceService.AuthDeviceUpdate(newAuthDevice);

                return new ResponseEntity<>(HttpStatus.OK);
            }//update
            catch(Exception e){
                return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
            }
            //return Response 200
        }
        Map<String, String> errors = new HashMap<>();
        errors.put("authdeviceId", "Auth Device with Id " +
                newAuthDevice.getAuthDeviceId() + " with direction " + newAuthDevice.getAuthDeviceDirection() +" does not exist");

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @PutMapping(path = "authdevice/entrance")
    public ResponseEntity<?> UpdateAuthDeviceEntrance(
            @RequestParam(name = "entranceid", required = false)
                    Long entranceid,@Valid @RequestBody List<AuthDevice> newAuthDevices) {



        if (newAuthDevices.size() != 2){
            Map<String, String> errors = new HashMap<>();
            errors.put("Error", "Requires a pair of auth device ids" );
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }


        try {
            AuthDevice authDevice1 = authDeviceService.findbyId(newAuthDevices.get(0).getAuthDeviceId()).get();
            AuthDevice authDevice2 = authDeviceService.findbyId(newAuthDevices.get(1).getAuthDeviceId()).get();

            if (entranceid!=null){
                if (entranceService.findById(entranceid).isEmpty() && entranceid != null){
                    Map<String, String> errors = new HashMap<>();
                    errors.put("Error", "EntranceId "+entranceid+" does not exist" );
                    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
                }

                // compare database entrance id
                if (entranceService.findById(entranceid).get().getUsed() == true){
                    if (!(authDevice1.getEntrance().getEntranceId().equals(entranceid))){
                        Map<String, String> errors = new HashMap<>();
                        errors.put("Error", "EntranceId "+entranceid+" is being used" );
                        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            if (authDevice1.getController().getControllerId() !=
                    authDevice2.getController().getControllerId()){
                Map<String, String> errors = new HashMap<>();
                errors.put("Error", "Auth Devices must belong to the same controller" );
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            if  (!(authDevice1.getAuthDeviceDirection().substring(0,2))
                    .equals(authDevice2.getAuthDeviceDirection().substring(0,2))){

                Map<String, String> errors = new HashMap<>();
                errors.put("Error", "Auth Devices must exist in pairs" );
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e){
            Map<String, String> errors = new HashMap<>();
            errors.put("Error", "Auth Devices do not exist" );
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        List <AuthDevice> updated = new ArrayList<>();

        for (int i=0; i<2; i++)
        {
            try{

                AuthDevice newSingleAuthDevice = newAuthDevices.get(i);
                AuthDevice authdevice = authDeviceService.findbyId(newSingleAuthDevice.getAuthDeviceId()).get();
                entranceService.setEntranceUsed(authdevice.getEntrance(),false);
                if ( entranceid == null && authdevice.getEntrance() != null){
                    // set previous entrance to not used
                    // set current to used
                    entranceService.setEntranceUsed(entranceService.findById(authdevice.getEntrance().getEntranceId()).get(),false);
                    updated.add(authDeviceService.AuthDeviceEntranceUpdate(authdevice, null));
                }
                else{
                    entranceService.setEntranceUsed(entranceService.findById(entranceid).get(),true);
                    updated.add(authDeviceService.AuthDeviceEntranceUpdate(authdevice, entranceService.findById(entranceid).get()));
                }

            }

            catch(Exception e){
                return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
                }
        }
            //return Response 200
        return new ResponseEntity<>(updated,HttpStatus.OK);
    }

    @DeleteMapping("/authdevice/delete/{authdeviceid}")
    public ResponseEntity<?> deleteauthdevice(@PathVariable Long authdeviceid){
        try {
            //System.out.println(controllerService.findById(controllerId).get());
            authDeviceService.deleteAuthDevice(authdeviceid);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/authdevice/reset/{authdeviceid}")
    public ResponseEntity<?> resetauthdevice(@PathVariable Long authdeviceid){
        try {
            //System.out.println(controllerService.findById(controllerId).get());
            authDeviceService.resetAuthDevice(authdeviceid);

            return new ResponseEntity<>( HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/controller/delete/{controllerId}")
    public ResponseEntity<?> deleteControllerWithId(@PathVariable Long controllerId){
        Optional<Controller> optionalController = controllerService.findById(controllerId);

        if (optionalController.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (optionalController.get().getMasterController() == true){
            Map<String, String> errors = new HashMap<>();
            errors.put("controllerId", "Controller with Id " +
                    controllerId + " with Serial No " + optionalController.get().getControllerSerialNo()+" is a Master Controller and cannot be edited.");
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Controller existingcontroller = optionalController.get();
        String IPaddress = existingcontroller.getControllerIP();
        //api call to get status
        try {
            controllerService.shutdownunicon(IPaddress);
            controllerService.deleteControllerWithId(controllerId);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        }
    }

    @DeleteMapping("/controller/reset/{controllerId}")
    public ResponseEntity<?> resetControllerWithId(@PathVariable Long controllerId){

        Optional<Controller> optionalController = controllerService.findById(controllerId);

        if (optionalController.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (optionalController.get().getMasterController() == true){
            Map<String, String> errors = new HashMap<>();
            errors.put("controllerId", "Controller with Id " +
                    controllerId + " with Serial No " + optionalController.get().getControllerSerialNo()+" is a Master Controller and cannot be edited.");
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Controller existingcontroller = optionalController.get();
        String IPaddress = existingcontroller.getControllerIP();
        //api call to get status
        try {
            controllerService.deleteControllerWithId(controllerId);

            if (!controllerService.backToDefault(IPaddress)){
                Map<String, String> errors = new HashMap<>();
                errors.put("controllerId", "Controller with Id " +
                        controllerId + " with Serial No " + optionalController.get().getControllerSerialNo()+" unable to reset.");
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

//            controllerService.triggerHealthcheck(IPaddress);


            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        }
    }


    @GetMapping("/availableEntrances")
    public List<Entrance> getAvailableEntrances() {
        return entranceService.getAvailableEntrances();
    }

    @GetMapping("/allAuthMethods")
    public List<AuthMethod> getAllAuthMethods() {
        return authMethodRepository.findAll();
    }

    @GetMapping("/controllerConnection/{controllerId}")
    public ResponseEntity<?> getControllerConnection(@PathVariable Long controllerId) throws Exception {

        Optional<Controller> optionalController = controllerService.findById(controllerId);

        if (optionalController.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Controller existingcontroller = optionalController.get();
        String IPaddress = existingcontroller.getControllerIP();
        //api call to get status
        try {
            ControllerConnection connection = controllerService.getControllerConnectionUnicon(IPaddress);
            if (connection == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            else {
                existingcontroller.setLastOnline(LocalDateTime.now(ZoneId.of("GMT+08:00")));
                existingcontroller.getAuthDevices().forEach((existingauthDevice -> {

                    String direction = existingauthDevice.getAuthDeviceDirection();
                    if ( existingauthDevice.getAuthDeviceDirection().equals("E1_IN") && connection.getE1_IN())
                    {
                        existingauthDevice.setLastOnline(LocalDateTime.now(ZoneId.of("GMT+08:00")));
                        authDeviceService.save(existingauthDevice);
                    }

                    if ( existingauthDevice.getAuthDeviceDirection().equals("E1_OUT") && connection.getE1_OUT())
                    {
                        existingauthDevice.setLastOnline(LocalDateTime.now(ZoneId.of("GMT+08:00")));
                        authDeviceService.save(existingauthDevice);
                    }

                    if ( existingauthDevice.getAuthDeviceDirection().equals("E2_IN") && connection.getE2_IN())
                    {
                        existingauthDevice.setLastOnline(LocalDateTime.now(ZoneId.of("GMT+08:00")));
                        authDeviceService.save(existingauthDevice);
                    }

                    if ( existingauthDevice.getAuthDeviceDirection().equals("E2_OUT") && connection.getE2_OUT())
                    {
                        existingauthDevice.setLastOnline(LocalDateTime.now(ZoneId.of("GMT+08:00")));
                        authDeviceService.save(existingauthDevice);
                    }

                }));
                controllerService.save(existingcontroller);

                return new ResponseEntity<>(connection, HttpStatus.OK);
            }
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        }
    }

    @PostMapping("/uniconUpdater")
    public ResponseEntity<?> testing() {
        List<Controller> response = uniconUpdater.updateUnicons();
        System.out.println(response);
        if (response.isEmpty()){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    }

}

