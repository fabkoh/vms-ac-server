package com.vmsac.vmsacserver.controller;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.repository.AuthDeviceRepository;
import com.vmsac.vmsacserver.service.AuthDeviceService;
import com.vmsac.vmsacserver.service.ControllerService;
import com.vmsac.vmsacserver.service.EntranceService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ControllerController {

    @Autowired
    private ControllerService controllerService;

    @Autowired
    private AuthDeviceService authDeviceService;

    @Autowired
    private EntranceService entranceService;



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
            @Valid @RequestBody FrontendControllerDto newFrontendControllerDto) {
        Optional<Controller> optionalController = controllerService.findBySerialNo(newFrontendControllerDto.getControllerSerialNo());

        if (optionalController.isPresent() && optionalController.get().getControllerId() == controllerId) {
            try {
                return new ResponseEntity<>(controllerService.FrondEndControllerUpdate(newFrontendControllerDto), HttpStatus.OK);
            }//update
            catch(Exception e){
                return ResponseEntity.badRequest().build();
            }
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

    @PutMapping(path = "authdevice/{authdeviceId}")
    public ResponseEntity<?> UpdateAuthDevice( @PathVariable Long authdeviceId,
                                               @Valid @RequestBody AuthDevice newAuthDevice) {
        Optional<AuthDevice> optionalAuthDevice = authDeviceService.findbyId(newAuthDevice.getAuthDeviceId());

        if (optionalAuthDevice.isPresent() && optionalAuthDevice.get().getAuthDeviceId() == authdeviceId) {
            try {
                return new ResponseEntity<>(authDeviceService.AuthDeviceUpdate(newAuthDevice), HttpStatus.OK);
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

        if (entranceService.findById(entranceid).isEmpty() && entranceid != null){
            Map<String, String> errors = new HashMap<>();
            errors.put("Error", "EntranceId "+entranceid+" does not exist" );
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        List <AuthDevice> updated = new ArrayList<>();

        for (int i=0; i<2; i++)
        {
            try{

                AuthDevice newSingleAuthDevice = newAuthDevices.get(i);
                AuthDevice authdevice = authDeviceService.findbyId(newSingleAuthDevice.getAuthDeviceId()).get();
                entranceService.setEntranceUsed(authdevice.getEntrance(),false);
                if ( entranceid == null ){
                    // set previous entrance to not used
                    // set current to used
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
    public ResponseEntity<?> resetauthdevice(@PathVariable Long authdeviceid){
        try {
            //System.out.println(controllerService.findById(controllerId).get());
            return new ResponseEntity<>(authDeviceService.resetAuthDevice(authdeviceid), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/controller/delete/{controllerId}")
    public ResponseEntity<?> deleteControllerWithId(@PathVariable Long controllerId){
        try {
            controllerService.backToDefault(controllerId);
            controllerService.shutdownunicon(controllerId);
            authDeviceService.deleteRelatedAuthDevices(controllerId);
            //System.out.println(controllerService.findById(controllerId).get());
            controllerService.deleteControllerWithId(controllerId);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/controller/reset/{controllerId}")
    public ResponseEntity<?> resetControllerWithId(@PathVariable Long controllerId){
        try {
            controllerService.backToDefault(controllerId);
            controllerService.rebootunicon(controllerId);
            authDeviceService.deleteRelatedAuthDevices(controllerId);
            controllerService.deleteControllerWithId(controllerId);


            return ResponseEntity.noContent().build();
        } catch (Exception e) {

            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/availableEntrances")
    public List<Entrance> getAvailableEntrances() {
        return entranceService.getAvailableEntrances();
    }


    @GetMapping("/controllerConnection/{controllerId}")
    public ResponseEntity<?> getControllerConnection(@PathVariable Long controllerId) throws Exception {

        String url = "http://192.168.1.135:5000/controller/status";
        //api call to get status
        ControllerConnection connection = controllerService.getControllerConnectionUnicon("");

        return new ResponseEntity<>(connection, HttpStatus.OK);
    }
}

