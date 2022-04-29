package com.vmsac.vmsacserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceSchedule;
import com.vmsac.vmsacserver.model.credential.Credential;
import com.vmsac.vmsacserver.repository.AuthDeviceRepository;
import com.vmsac.vmsacserver.repository.ControllerRepository;
import com.vmsac.vmsacserver.repository.EntranceRepository;
import com.vmsac.vmsacserver.repository.EntranceScheduleRepository;
import com.vmsac.vmsacserver.repository.AccessGroupEntranceNtoNRepository;
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
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class ControllerService {


    String pinAssignment = "{'E1_IN_D0': '14', 'E1_IN_D1': '15', 'E1_IN_Buzz': '23', 'E1_IN_Led': '18', 'E1_OUT_D0': '2', 'E1_OUT_D1': '3', 'E1_OUT_Buzz': '17', 'E1_OUT_Led': '4', 'E1_Mag': '6', 'E1_Button': '5','E2_IN_D0': '24', 'E2_IN_D1':'25', 'E2_IN_Buzz': '7', 'E2_IN_Led': '8', 'E2_OUT_D0': '22', 'E2_OUT_D1': '10', 'E2_OUT_Buzz': '11', 'E2_OUT_Led': '9', 'E2_Mag': '26', 'E2_Button': '12', 'Relay_1': '27', 'Relay_2': '13', 'Fire': '26', 'Gen_In_1': '16', 'Gen_Out_1': '', 'Gen_In_2': '20', 'Gen_Out_2': '', 'Gen_In_3': '21', 'Gen_Out_3': ''}";
    String settingsConfig = "testsettings";



    @Autowired
    private AuthDeviceRepository authDeviceRepository;

    @Autowired
    private AccessGroupEntranceNtoNRepository accessGroupEntranceNtoNRepository;

    @Autowired
    private ControllerRepository controllerRepository;

    @Autowired
    EntranceScheduleRepository entranceScheduleRepository;

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
        String ip = InetAddress.getLoopbackAddress().getHostAddress();
        Boolean status = false;
        if (uniconControllerDto.getControllerIP() == ip){
            status = true;
        }

        return controllerRepository.save(uniconControllerDto.toCreateController(uniconControllerDto.getControllerSerialNo(),
                LocalDateTime.now(ZoneId.of("GMT+08:00")),status,LocalDateTime.now(ZoneId.of("GMT+08:00")),pinAssignment,settingsConfig,false)).touniconDto();
    }



    public UniconControllerDto uniconControllerUpdate(UniconControllerDto uniconControllerDto) throws Exception{
        controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(uniconControllerDto.getControllerSerialNo())
                .orElseThrow(() -> new RuntimeException("Controller does not exist"));

        Controller existingcontroller = (((controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(uniconControllerDto.getControllerSerialNo())).get()));

        if ( (existingcontroller.getControllerId() == uniconControllerDto.getControllerId()) ||
                Objects.isNull(uniconControllerDto.getControllerId()) ){


            Controller toSave = uniconControllerDto.toController();
            toSave.setControllerId(existingcontroller.getControllerId());
            toSave.setControllerName(existingcontroller.getControllerName());
            toSave.setLastOnline(LocalDateTime.now(ZoneId.of("GMT+08:00")));
            toSave.setPinAssignmentConfig(pinAssignment);
            toSave.setSettingsConfig(settingsConfig);
            toSave.setPendingIP(existingcontroller.getPendingIP());
            toSave.setMasterController(existingcontroller.getMasterController());
            toSave.setCreated(existingcontroller.getCreated());

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


            existingcontroller.setControllerName(newFrontendControllerDto.getControllerName());
            existingcontroller.setControllerIP(newFrontendControllerDto.getControllerIP());
            existingcontroller.setControllerIPStatic(newFrontendControllerDto.getControllerIPStatic());

            return controllerRepository.save(existingcontroller).toFrontendDto();


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

    public void shutdownunicon(String IPaddress) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://"+IPaddress+":5000/api/shutdown";
        HttpEntity<String> request = new HttpEntity<String>("");

        ResponseEntity<String> productCreateResponse =
                restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);

        return;
    }

    public void rebootunicon(String IPaddress) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://"+IPaddress+":5000/api/reboot";
        HttpEntity<String> request = new HttpEntity<String>("");

        ResponseEntity<String> productCreateResponse =
                restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);

        return;
    }

    public Boolean backToDefault(String IPaddress) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        String resourceUrl = "http://"+IPaddress+":5000/api/reset";
        HttpEntity<String> request = new HttpEntity<String>("");

        ResponseEntity<String> productCreateResponse =
                restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);

        if (productCreateResponse.getStatusCodeValue() == 200){
            return true;
        }
        else{
            return false;
        }
    }

    public ControllerConnection getControllerConnectionUnicon(String IPaddress) throws Exception {
            RestTemplate restTemplate = new RestTemplate();

            String resourceUrl = "http://"+IPaddress+":5000/api/status";
            HttpEntity<String> request = new HttpEntity<String>("");

            ResponseEntity<String> productCreateResponse =
                    restTemplate.exchange(resourceUrl, HttpMethod.GET, request, String.class);

            if (productCreateResponse.getStatusCodeValue() == 200){
                ObjectMapper mapper = new ObjectMapper();
                ControllerConnection connection = mapper.readValue(productCreateResponse.getBody(), ControllerConnection.class);

                return connection;
            }
            else{
                return null;
            }

    }


    public HttpStatus generate(){
        try {

            Controller existingcontroller = controllerRepository.getById(1L);

            String test ="";

            for ( int i=0; i<2;i++){
                Entrance existingentrance = existingcontroller.getAuthDevices().get(i*2).getEntrance();
                EntranceSchedule exisitngEntranceSchedule = entranceScheduleRepository.findByEntranceIdEqualsAndDeletedIsFalse(existingentrance.getEntranceId());



                Map<String,Object> entrance1 = new LinkedHashMap<>();
                entrance1.put("Entrance",existingentrance.getEntranceName());
                entrance1.put("EntranceSchedule",exisitngEntranceSchedule);

                Map<String,Object> existingentrancedetails = new LinkedHashMap<>();
                existingentrancedetails.put("Antipassback","No");
                existingentrancedetails.put("Zone","ZoneId");

                Map<String,Object> authdevices = new LinkedHashMap<>();
                authdevices.put("Device1",authDeviceRepository.findByEntrance_EntranceIdIsAndAuthDeviceDirectionContains(existingentrance.getEntranceId(),"IN"));
                authdevices.put("Device2",authDeviceRepository.findByEntrance_EntranceIdIsAndAuthDeviceDirectionContains(existingentrance.getEntranceId(),"OUT"));
                existingentrancedetails.put("AuthenticationDevices",authdevices);

                Map<String,Object> accessgroups = new LinkedHashMap<>();
                // for all access group in entrances
//                accessgroups.put()
                existingentrancedetails.put("AccessGroups",accessgroups);


                entrance1.put("EntranceDetails",existingentrancedetails);

                System.out.println(entrance1);

            }


            RestTemplate restTemplate = new RestTemplate();
            String resourceUrl = "http://192.168.1.135:5000/credOccur";

            String temp = "[{'Entrance': 'SideDoor', 'EntranceSchedule': {'2022-04-04': [{'starttime': '08:00', 'endtime': '19:00'}]}, 'EntranceDetails': {'Antipassback': 'No', 'Zone': 'ZoneId', 'AuthenticationDevices': {'Device1': {'Masterpassword': '666666', 'Direction': 'IN', 'AuthMethod': [{'Method': 'Card,Pin', 'Schedule': {'2022-03-14': [{'starttime': '18:00', 'endtime': '23:00'}], '2022-04-04': [{'starttime': '18:00', 'endtime': '23:00'}]}}, {'Method': 'Card,Pin', 'Schedule': {'2022-03-14': [{'starttime': '09:00', 'endtime': '18:00'}], '2022-04-04': [{'starttime': '09:00', 'endtime': '23:59'}]}}]}, 'Device2': {'Masterpassword': '111111', 'Direction': 'OUT', 'AuthMethod': [{'Method': 'Card', 'Schedule': {'2022-03-14': [{'starttime': '18:00', 'endtime': '23:00'}], '2022-04-04': [{'starttime': '09:00', 'endtime': '23:59'}]}}, {'Method': 'Card', 'Schedule': {'2022-03-14': {'starttime': '18:00', 'endtime': '23:00'}, '2022-04-04': {'starttime': '09:00', 'endtime': '18:00'}}}]}}, 'AccessGroups': [{'AccessGroup1': {'Persons': [{'Name': 'John', 'Credentials': {'Card': '36443438', 'PIN':'123456', 'Fingerprint': 's1e97ncksiu'}}, {'Name': 'Kerry', 'Credentials': {'Card': '36443419', 'PIN': '123456', 'Fingerprint': 'ege56g4er'}}, {'Name': 'Steven', 'Credentials': {'Card': '47567858679', 'PIN': '85757', 'Fingerprint': 'gd15hry51r'}}], 'Schedule': {'2022-03-14': [{'starttime': '18:00', 'endtime': '23:00'}], '2022-04-04': [{'starttime': '09:00', 'endtime': '23:59'}]}}}, {'AccessGroup2': {'Persons': [{'Name': 'Mike', 'Credentials': {'Card': '6451234653', 'PIN': '123132', 'Fingerprint': 'ui233211sd'}}, {'Name': 'Keith', 'Credentials': {'Card': '12345983', 'PIN': '456456', 'Fingerprint': '654tyjyt5j132'}}, {'Name': 'Ron', 'Credentials': {'Card': '98794621', 'PIN': '654321', 'Fingerprint': '46ytj546r5th'}}], 'Schedule': {'2022-03-17': {'starttime': '18:00', 'endtime': '23:00'}, '2022-04-04': {'starttime': '09:00', 'endtime': '18:00'}}}}, {'AccessGroup3': {'Persons': [{'Name': 'Kim', 'Credentials': {'Card': '8657345345', 'PIN': '987654', 'Fingerprint': 'tyh56r4h5r4'}}, {'Name': 'Tan', 'Credentials': {'Card': '78654353245', 'PIN': '456789', 'Fingerprint': '84we56rh'}}, {'Name': 'Lee', 'Credentials': {'Card': '74563452354', 'PIN': '123456', 'Fingerprint': 'errg456e4ty'}}], 'Schedule': {'2022-03-14': {'starttime': '18:00', 'endtime': '23:00'}, '2022-04-04': {'starttime': '09:00', 'endtime': '18:00'}}}}], 'EventActionTriggers': [{'Trigger1': {'inputEventActions': {'magContactOpenNoTimer': '', 'magContactOpenWithTimer': '5secs'}, 'outputEventActions': {'cardReaderBuzzer': ''}, 'Schedule': {'2022-03-14': {'starttime': '18:00', 'endtime': '23:00'}, '2022-04-04': {'starttime': '09:00', 'endtime': '18:00'}}}}, {'Trigger2': {'inputEventActions': {'cardReaderBuzzerWithTimer': '60secs'}, 'outputEventActions': {'emailNotification': ''}, 'Schedule': {'2022-03-14': {'starttime': '18:00', 'endtime': '23:00'}, '2022-03-18': {'starttime': '09:00', 'endtime': '18:00'}}}}]}}, {'Entrance': 'MainDoor', 'EntranceSchedule': {'2022-03-18': {'starttime': '08:00', 'endtime': '09:00'}}, 'EntranceDetails': {'Zone': 'ZoneId', 'AuthenticationDevices': {'Device1': {'Direction': 'IN', 'AuthMethod': [{'Method': 'Fingerprint,Card', 'Schedule': {'2022-03-14': {'starttime': '18:00', 'endtime': '23:00'}, '2022-03-16': {'starttime': '18:00', 'endtime': '23:00'}}}, {'Method': 'Card,Pin', 'Schedule': {'2022-03-14': {'starttime': '09:00', 'endtime': '18:00'}, '2022-03-18': {'starttime': '09:00', 'endtime': '18:00'}}}]}, 'Device2': {'Direction': 'OUT', 'AuthMethod': [{'Method': 'Fingerprint', 'Schedule': {'2022-03-14': {'starttime': '18:00', 'endtime': '23:00'}, '2022-03-18': {'starttime': '18:00', 'endtime': '23:00'}}}, {'Method': 'Fingerprint,Pin', 'Schedule': {'2022-03-14': {'starttime': '18:00', 'endtime': '23:00'}, '2022-03-18': {'starttime': '09:00', 'endtime': '18:00'}}}]}}, 'AccessGroups': [{'AccessGroup1': {'Persons': [{'Name': 'John', 'Credentials': {'Card': '696955874', 'PIN': '225588', 'Fingerprint': 's1e97ncksiu'}}, {'Name': 'Kerry', 'Credentials': {'Card': '696955874', 'PIN': '225588', 'Fingerprint': 's1e97ncksiu'}}, {'Name': 'Steven', 'Credentials': {'Card': '696955874', 'PIN': '225588', 'Fingerprint': 's1e97ncksiu'}}], 'Schedule': {'2022-03-14': {'starttime': '18:00', 'endtime': '23:00'}, '2022-03-18': {'starttime': '09:00', 'endtime': '18:00'}}}}, {'AccessGroup2': {'Persons': [{'Name': 'Mike', 'Credentials': {'Card': '6451234653', 'PIN': '123132', 'Fingerprint': 'ui233211sd'}}, {'Name': 'Keith', 'Credentials': {'Card': '12345983', 'PIN': '456456', 'Fingerprint': '654tyjyt5j132'}}, {'Name': 'Ron', 'Credentials': {'Card': '98794621', 'PIN': '654321', 'Fingerprint': '46ytj546r5th'}}], 'Schedule': {'2022-03-17': {'starttime': '18:00', 'endtime': '23:00'}, '2022-03-18': {'starttime': '09:00', 'endtime': '18:00'}}}}, {'AccessGroup3': {'Persons': [{'Name': 'Kim', 'Credentials': {'Card': '8657345345', 'PIN': '987654', 'Fingerprint': 'tyh56r4h5r4'}}, {'Name': 'Tan', 'Credentials': {'Card': '78654353245', 'PIN': '456789', 'Fingerprint': '84we56rh'}}, {'Name': 'Lee', 'Credentials': {'Card': '74563452354', 'PIN': '123456', 'Fingerprint': 'errg456e4ty'}}], 'Schedule': {'2022-03-14': {'starttime': '18:00', 'endtime': '23:00'}, '2022-03-18': {'starttime': '09:00', 'endtime': '18:00'}}}}], 'EventActionTriggers': [{'Trigger1': {'inputEventActions': {'magContactOpenNoTimer': '', 'magContactOpenWithTimer': '5secs'}, 'outputEventActions': {'cardReaderBuzzer': ''}, 'Schedule': {'2022-03-14': {'starttime': '18:00', 'endtime': '23:00'}, '2022-03-18': {'starttime': '09:00', 'endtime': '18:00'}}}}, {'Trigger2': {'inputEventActions': {'cardReaderBuzzerWithTimer': '60secs'}, 'outputEventActions': {'emailNotification': ''}, 'Schedule': {'2022-03-14': {'starttime': '18:00', 'endtime': '23:00'}, '2022-03-18': {'starttime': '09:00', 'endtime': '18:00'}}}}]}}]";








            String Entrance1 ="'Entrance':";
            String Entrance2 = "";
            String credSchedule = "[]";

            HttpEntity<String> request = new HttpEntity<String>
                    (credSchedule);

            ResponseEntity<String> productCreateResponse =
                    restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);

            System.out.println(productCreateResponse);
            System.out.println(productCreateResponse.getBody());
            return HttpStatus.OK;
        }
        catch(Exception e){
            System.out.println(e);
            return HttpStatus.BAD_REQUEST;
        }

    }

    public void save(Controller existingcontroller) {
        controllerRepository.save(existingcontroller);
    }

    public Boolean IsIPavailable(String ipAddress)
            throws UnknownHostException, IOException
    {
        InetAddress geek = InetAddress.getByName(ipAddress);
        System.out.println("Sending Ping Request to " + ipAddress);
        if (geek.isReachable(5000))
            return false;
        else
            return true;
    }

    public Boolean isNotValidInet4Address(String ip)
    {
        String[] splitString = ip.split("[.]");
        if (splitString.length > 4) {
            return true;
        }
        for (String string : splitString) {
            if (string.isEmpty()) {
                return true;
            }
            if (!string.matches("[0-9]{1,3}")) {
                return true;
            }
            int number = Integer.parseInt(string);
            if (!(number >= 0 && number <= 255)) {
                return true;
            }
        }
        return false;
    }

    public Boolean UpdateUniconIP(FrontendControllerDto newFrontendControllerDto) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        Controller existingController = controllerRepository.findById(newFrontendControllerDto.getControllerId()).get();
        existingController.setPendingIP(newFrontendControllerDto.getControllerIP());
        controllerRepository.save(existingController);

        String resourceUrl = "http://"+ existingController.getControllerIP()+":5000/api/config";

        Map<String,Object> requestBody = new LinkedHashMap<>();
        requestBody.put("controllerIPStatic",existingController.getControllerIPStatic());
        requestBody.put("controllerIP",newFrontendControllerDto.getControllerIP());
        requestBody.put("controllerSerialNo",newFrontendControllerDto.getControllerSerialNo());

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(requestBody);

        HttpEntity<String> request = new HttpEntity<String>(json);

        try{
            ResponseEntity<String> productCreateResponse =
                    restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);
            return false;
        }
        catch(Exception e){

            long startTime = System.currentTimeMillis(); //fetch starting time
            // get response
            while((System.currentTimeMillis()-startTime)<10000){
                Thread.sleep(1000);
                try{
                    if ( getControllerConnectionUnicon(newFrontendControllerDto.getControllerIP()) != null){
                        return true;
                }}
                catch (Exception e1){}
                }
            return false;
            }
        }
}
