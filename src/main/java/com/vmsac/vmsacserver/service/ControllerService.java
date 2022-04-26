package com.vmsac.vmsacserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmsac.vmsacserver.model.Controller;
import com.vmsac.vmsacserver.model.ControllerConnection;
import com.vmsac.vmsacserver.model.FrontendControllerDto;
import com.vmsac.vmsacserver.model.UniconControllerDto;
import com.vmsac.vmsacserver.model.credential.Credential;
import com.vmsac.vmsacserver.repository.ControllerRepository;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Null;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class ControllerService {


    String pinAssignment = "testpin";
    String settingsConfig = "testsettings";



    @Autowired
    private ControllerRepository controllerRepository;

    public List<Controller> findAllNotDeleted() {
        return controllerRepository.findByDeleted(false).stream()
                .collect(Collectors.toList());
    }

    public Optional<Controller> findById (Long controllerId) {
        return controllerRepository.findByControllerIdEqualsAndDeletedFalse(controllerId);

    }

    public Optional<Controller> findBySerialNo (String controllerSerialNo) {
        return controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(controllerSerialNo);

    }

    public UniconControllerDto uniconControllerCreate(UniconControllerDto uniconControllerDto){

        return controllerRepository.save(uniconControllerDto.toCreateController(uniconControllerDto.getControllerSerialNo(),
                LocalDateTime.now(ZoneId.of("GMT+08:00")),pinAssignment,settingsConfig,false)).touniconDto();
    }



    public UniconControllerDto uniconControllerUpdate(UniconControllerDto uniconControllerDto) throws Exception{
        controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(uniconControllerDto.getControllerSerialNo())
                .orElseThrow(() -> new RuntimeException("Controller does not exist"));

        Controller existingcontroller = (((controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(uniconControllerDto.getControllerSerialNo())).get()));

        if ( (existingcontroller.getControllerId() == uniconControllerDto.getControllerId()) ||
                Objects.isNull(uniconControllerDto.getControllerId()) ){
            String controllerName = (existingcontroller.getControllerName());


            Controller toSave = uniconControllerDto.toController();
            toSave.setControllerId(existingcontroller.getControllerId());
            toSave.setControllerName(existingcontroller.getControllerName());
            toSave.setLastOnline(LocalDateTime.now(ZoneId.of("GMT+08:00")));
            toSave.setPinAssignmentConfig(pinAssignment);
            toSave.setSettingsConfig(settingsConfig);

            return controllerRepository.save(toSave).touniconDto();


        }
        throw new RuntimeException("Controller Id clashes");


    }

    public FrontendControllerDto FrondEndControllerUpdate(FrontendControllerDto newFrontendControllerDto) throws Exception{
        controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(newFrontendControllerDto.getControllerSerialNo())
                .orElseThrow(() -> new RuntimeException("Controller does not exist"));

        Controller existingcontroller = (((controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(newFrontendControllerDto.getControllerSerialNo())).get()));

        if ( (existingcontroller.getControllerId() == newFrontendControllerDto.getControllerId()) ||
                Objects.isNull(newFrontendControllerDto.getControllerId()) ){
            String controllerName = (existingcontroller.getControllerName());


            Controller toSave = newFrontendControllerDto.toController();

            toSave.setPinAssignmentConfig(existingcontroller.getPinAssignmentConfig());
            toSave.setSettingsConfig(existingcontroller.getSettingsConfig());
            toSave.setLastOnline(existingcontroller.getLastOnline());
            toSave.setAuthDevices(existingcontroller.getAuthDevices());

            return controllerRepository.save(toSave).toFrontendDto();


        }
        throw new RuntimeException("Controller Id clashes");


    }

    public void deleteControllerWithId(Long controllerId) throws Exception {
        Controller toDeleted = controllerRepository.findByControllerIdEqualsAndDeletedFalse(controllerId)
                .orElseThrow(() -> new RuntimeException("Controller does not exist"));

        toDeleted.setDeleted(true);
        toDeleted.setAuthDevices(Collections.emptyList());
        controllerRepository.save(toDeleted);
    }

    public void shutdownunicon(Long controllerId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://localhost:5000/shutdown";
        HttpEntity<String> request = new HttpEntity<String>("");

        ResponseEntity<String> productCreateResponse =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, request, String.class);

        System.out.println(productCreateResponse);
    }

    public void rebootunicon(Long controllerId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://localhost:5000/reboot";
        HttpEntity<String> request = new HttpEntity<String>("");

        ResponseEntity<String> productCreateResponse =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, request, String.class);

        System.out.println(productCreateResponse);
    }

    public void backToDefault(Long controllerId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://localhost:5000/controller";

        HttpEntity<String> request = new HttpEntity<String>(findById(controllerId).get().toString());

        ResponseEntity<String> productCreateResponse =
                restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);

//        System.out.println(productCreateResponse.getStatusCodeValue());
//        System.out.println(productCreateResponse.getBody());
//
//        System.out.println(controllerRepository.findByControllerIdEquals(controllerId).get().toString());
    }

    public ControllerConnection getControllerConnectionUnicon(String IPaddress) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://192.168.1.135:5000/controller/status";

        HttpEntity<String> request = new HttpEntity<String>("");

        ResponseEntity<String> productCreateResponse =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, request, String.class);

        System.out.println(productCreateResponse);
        System.out.println(productCreateResponse.getBody());

        ObjectMapper mapper = new ObjectMapper();
        ControllerConnection connection = mapper.readValue(productCreateResponse.getBody(), ControllerConnection.class);


        return connection;
    }
}
