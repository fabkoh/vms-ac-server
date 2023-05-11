package com.vmsac.vmsacserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.accessgroupentrance.AccessGroupEntranceNtoN;
import com.vmsac.vmsacserver.model.accessgroupschedule.AccessGroupScheduleDto;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodSchedule;
import com.vmsac.vmsacserver.model.authmethodschedule.AuthMethodScheduleDto;
import com.vmsac.vmsacserver.model.credential.CredentialDto;
import com.vmsac.vmsacserver.model.credentialtype.entranceschedule.EntranceSchedule;
import com.vmsac.vmsacserver.repository.*;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


@Service
public class ControllerService {


    String pinAssignment = "{'E1_IN_D0': '14'}";
    String settingsConfig = "testsettings";



    @Autowired
    private AuthDeviceRepository authDeviceRepository;

    @Autowired
    private AuthMethodScheduleService authMethodScheduleService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private AccessGroupScheduleService accessGroupScheduleService;

    @Autowired
    private PersonService personService;

    @Autowired
    private EventsManagementService emService;

    @Autowired
    private InputEventRepository inputEventRepo;

    @Autowired
    private OutputEventRepository outputEventRepo;

    @Autowired
    private AccessGroupEntranceNtoNRepository accessGroupEntranceNtoNRepository;

    @Autowired
    private ControllerRepository controllerRepository;

    @Autowired
    private EntranceRepository entranceRepo;

    @Autowired
    private GENConfigsRepository genRepo;

    @Autowired
    private TriggerSchedulesRepository triggerSchedulesRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    EntranceScheduleRepository entranceScheduleRepository;

    @Autowired
    AuthMethodScheduleRepository authMethodScheduleRepository;

    public List<Controller> findAllNotDeleted() {
        return controllerRepository.findByDeletedIsFalseOrderByCreatedDesc().stream()
                .collect(Collectors.toList());
    }

    public Optional<Controller> findById (Long controllerId) {
        return controllerRepository.findByControllerIdEqualsAndDeletedFalse(controllerId);

    }

    public Optional<Controller> findBySerialNo (String controllerSerialNo) {
        return controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(controllerSerialNo);

    }

    public Boolean existsByControllerNameEquals (String controllerName) {
        return controllerRepository.existsByControllerNameEqualsAndDeletedFalse(controllerName);

    }

    public UniconControllerDto uniconControllerCreate(UniconControllerDto uniconControllerDto){
        String ip = InetAddress.getLoopbackAddress().getHostAddress();
        Boolean status = false;
        if (uniconControllerDto.getControllerIP() == ip){
            status = true;
        }

        return controllerRepository.save(uniconControllerDto.toCreateController(uniconControllerDto.getControllerSerialNo(),
                LocalDateTime.now(ZoneId.of("GMT+08:00")), null, status,LocalDateTime.now(ZoneId.of("GMT+08:00")),pinAssignment,settingsConfig,false)).touniconDto();
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
            toSave.setLastSync(existingcontroller.getLastSync());
            return controllerRepository.save(toSave).touniconDto();
        }
        throw new RuntimeException("Controller Id clashes");
    }

    public FrontendControllerDto FrondEndControllerUpdate(FrontendControllerDto newFrontendControllerDto) throws Exception{

//        entityManager.getEntityManagerFactory().getCache().evictAll();
        controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(newFrontendControllerDto.getControllerSerialNo())
                .orElseThrow(() -> new RuntimeException("Controller does not exist"));

        Controller existingcontroller = controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(newFrontendControllerDto.getControllerSerialNo()).get();

        if ( (existingcontroller.getControllerId() == newFrontendControllerDto.getControllerId()) ||
                Objects.isNull(newFrontendControllerDto.getControllerId()) ){

            if (newFrontendControllerDto.getControllerIPStatic() == true){
                existingcontroller.setControllerIP(newFrontendControllerDto.getControllerIP());
                existingcontroller.setControllerIPStatic(newFrontendControllerDto.getControllerIPStatic());
            }

            existingcontroller.setControllerName(newFrontendControllerDto.getControllerName());

            return controllerRepository.save(existingcontroller).toFrontendDto();


        }
        throw new RuntimeException("Controller Id clashes");


    }

    public void deleteControllerWithId(Long controllerId) throws Exception {
        Controller toDeleted = controllerRepository.findByControllerIdEqualsAndDeletedFalse(controllerId)
                .orElseThrow(() -> new RuntimeException("Controller does not exist"));

        toDeleted.setControllerName(toDeleted.getControllerSerialNo());
        toDeleted.setDeleted(true);
        toDeleted.setPendingIP(null);
        toDeleted.setAuthDevices(Collections.emptyList());

        //set authMethodSchedules deleted to true
        List<AuthMethodSchedule> toDeleteSched = authMethodScheduleRepository.findByAuthDevice_Controller_ControllerId(controllerId);
        toDeleteSched.forEach(authMethodSchedule -> authMethodSchedule.setDeleted(true));
        authMethodScheduleRepository.saveAll(toDeleteSched);

        controllerRepository.save(toDeleted);
    }

    public void shutdownunicon(String IPaddress) {

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(3000);
        httpRequestFactory.setConnectTimeout(3000);
        httpRequestFactory.setReadTimeout(3000);

        System.out.println("---Shutting down unicon");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory);
        String resourceUrl = "http://"+IPaddress+":5000/api/shutdown";
        HttpEntity<String> request = new HttpEntity<String>("");

        try{
            ResponseEntity<String> productCreateResponse =
                    restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);
            System.out.println("--Finished shutting down");
        }
        catch(Exception e){
            return;
        }
        return;
    }

    public void rebootunicon(String IPaddress) {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://"+IPaddress+":5000/api/reboot";
        HttpEntity<String> request = new HttpEntity<String>("");

        try{
            ResponseEntity<String> productCreateResponse =
                    restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);
        }
        catch(Exception e){
            return;
        }
        return;
    }

    public Boolean backToDefault(String IPaddress) throws Exception {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(3000);
        httpRequestFactory.setConnectTimeout(3000);
        httpRequestFactory.setReadTimeout(3000);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        String resourceUrl = "http://"+IPaddress+":5000/api/reset";
        HttpEntity<String> request = new HttpEntity<String>("");

        try{

            ResponseEntity<String> productCreateResponse =
                    restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);
            return false;
        }
        catch(Exception e){
            {
                Thread.sleep(8000);
//                LocalDateTime lastonlinedatetime = controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(existingController
//                        .getControllerSerialNo()).get().getLastOnline();
//
//                LocalDateTime currentdatetime = LocalDateTime.now(ZoneId.of("GMT+08:00"));
//
//                if (lastonlinedatetime.isAfter(currentdatetime.minusSeconds(30))) {
//                    return true;
//                }
                return true;
            }

        }
    }

    public ControllerConnection getControllerConnectionUnicon(String IPaddress) throws Exception {

            HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setConnectionRequestTimeout(3000);
            httpRequestFactory.setConnectTimeout(3000);
            httpRequestFactory.setReadTimeout(3000);

            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

            String resourceUrl = "http://"+IPaddress+":5000/api/status";
            HttpEntity<String> request = new HttpEntity<String>("");

            try{
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
            catch (Exception e){
                return null;
            }

    }

    public ControllerConnection triggerHealthcheck(String IPaddress) throws Exception {

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(3000);
        httpRequestFactory.setConnectTimeout(3000);
        httpRequestFactory.setReadTimeout(3000);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        String resourceUrl = "http://"+IPaddress+":5000/api/healthcheck";
        HttpEntity<String> request = new HttpEntity<String>("");

        try{
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
        catch (Exception e){
            return null;
        }

    }

    public HttpStatus sendEntranceNameRelationship(Long controllerId) throws Exception{

        try{
            Controller existingcontroller = controllerRepository.getById(controllerId);
            String IPaddress = existingcontroller.getControllerIP();

            RestTemplate restTemplate = new RestTemplate();
            String resourceUrl = "http://"+IPaddress+":5000/api/entrance-name";

            Map <String,Object> jsonbody = new HashMap();
            jsonbody.put("controllerSerialNo",existingcontroller.getControllerSerialNo());


            try{
                Long entranceId1 = existingcontroller.getAuthDevices().stream()
                        .filter(ad -> ad.getAuthDeviceDirection().equals("E1_IN"))
                        .findFirst()
                        .get().getEntrance().getEntranceId();
                jsonbody.put("E1", entranceId1);
            }
            catch(Exception e){
                jsonbody.put("E1","");
            }

            try{
                Long entranceId2 = existingcontroller.getAuthDevices().stream()
                        .filter(ad -> ad.getAuthDeviceDirection().equals("E2_IN"))
                        .findFirst()
                        .get().getEntrance().getEntranceId();
                jsonbody.put("E2", entranceId2);
            }
            catch(Exception e){
                jsonbody.put("E2","");
            }

            HttpEntity<Map> request = new HttpEntity<>
                    (jsonbody);

            ResponseEntity<String> productCreateResponse =
                    restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);

            if (productCreateResponse.getStatusCodeValue() == 204){
//                ObjectMapper mapper = new ObjectMapper();
//                ControllerConnection connection = mapper.readValue(productCreateResponse.getBody(), ControllerConnection.class);
                return HttpStatus.OK;
            }
            else{
                throw new Exception("API call fail");
            }
        }

        catch(Exception e){
            System.out.println(e);
        }
        throw new Exception("An error occurred");
    }

    public HttpStatus generate(Long controllerId) throws Exception {

            // find controller object
            Controller existingcontroller = controllerRepository.getById(controllerId);
            String MASTERPASSWORD = "666666";

            List<Object> RulesSet = new ArrayList<>(1);

            // iterate twice, find entrance 1 and 2 related to controller
            for ( int i=1; i<3; i++) {
//                try {
                // find entrance object
                int finalI = i;
                Entrance existingentrance = existingcontroller.getAuthDevices().stream()
                        .filter(ad -> ad.getAuthDeviceDirection().equals("E" + finalI + "_IN"))
                        .findFirst()
                        .get().getEntrance();

                if (!(existingentrance == null) && existingentrance.getIsActive() ) {
                    // find entrance-schedule object related to entrance
                    List<EntranceSchedule> exisitngEntranceSchedules = entranceScheduleRepository.findByEntranceIdAndDeletedFalseAndIsActiveTrue(existingentrance.getEntranceId());

                    Map<String, Object> entrance = new HashMap();
                    entrance.put("Entrance", existingentrance.getEntranceId());

                    // resolving rrule
                    try {
                        entrance.put("EntranceSchedule", GetEntranceScheduleObjectWithTime(exisitngEntranceSchedules));
                    } catch (Exception e) {
                        throw new Exception ("ERROR WITH EntranceSchedule: " + e);
                    }

                    Map<String, Object> existingentrancedetails = new HashMap();
                    existingentrancedetails.put("Antipassback", "No");
                    existingentrancedetails.put("Zone", "ZoneId");

                    Map<String, Object> authdevices = new HashMap();

                    AuthDevice exisitngDevice1 = authDeviceRepository.findByEntrance_EntranceIdIsAndAuthDeviceDirectionContains(existingentrance.getEntranceId(), "IN");
                    AuthDevice exisitngDevice2 = authDeviceRepository.findByEntrance_EntranceIdIsAndAuthDeviceDirectionContains(existingentrance.getEntranceId(), "OUT");

                    Map<String, Object> Device1 = new HashMap();

                    if (exisitngDevice1.getMasterpin() == true) {
                        Device1.put("Masterpassword", MASTERPASSWORD);
                    } else {
                        Device1.put("Masterpassword", false);
                    }

                    Device1.put("Direction", exisitngDevice1.getAuthDeviceDirection().substring(3));
                    Device1.put("defaultAuthMethod", exisitngDevice1.getDefaultAuthMethod().getAuthMethodDesc());
                    try {
                        Device1.put("AuthMethod", GetAuthMethodScheduleObjectWithTime(authMethodScheduleService.findByDeviceIdAndIsActive(exisitngDevice1.getAuthDeviceId())));
                    } catch (Exception e) {
                        throw new Exception ("ERROR WITH AUTH METHOD SCHEDULE 1: " + e);
                    }

                    Map<String, Object> Device2 = new HashMap();

                    if (exisitngDevice2.getMasterpin() == true) {
                        Device2.put("Masterpassword", MASTERPASSWORD);
                    } else {
                        Device2.put("Masterpassword", false);
                    }

                    Device2.put("Direction", exisitngDevice2.getAuthDeviceDirection().substring(3));
                    Device2.put("defaultAuthMethod", exisitngDevice2.getDefaultAuthMethod().getAuthMethodDesc());
                    try {
                        Device2.put("AuthMethod", GetAuthMethodScheduleObjectWithTime(authMethodScheduleService.findByDeviceIdAndIsActive(exisitngDevice2.getAuthDeviceId())));
                    } catch (Exception e) {
                        throw new Exception("ERROR WITH AUTH METHOD SCHEDULE 2: " + e);
                    }

                    authdevices.put("IN", Device1);
                    authdevices.put("OUT", Device2);

                    existingentrancedetails.put("AuthenticationDevices", authdevices);

                    // for all access group in entrances
//
                    List<Map> accessGroups = new ArrayList<Map>(1);

                    List<AccessGroupEntranceNtoN> listOfAccessGroupsNtoN = accessGroupEntranceNtoNRepository.findAllByEntranceEntranceIdAndDeletedFalse(existingentrance.getEntranceId());

                    for (AccessGroupEntranceNtoN accessGroupEntranceNtoN : listOfAccessGroupsNtoN) {
                        if ( accessGroupEntranceNtoN.getAccessGroup().getIsActive()){
                        List<Person> ListofPersons = personService.findByAccGrpId((accessGroupEntranceNtoN.getAccessGroup().getAccessGroupId()), false);
                        List<AccessGroupScheduleDto> ListofSchedule = accessGroupScheduleService.findAllByGroupToEntranceIdInAndIsActiveTrue(Collections.singletonList(accessGroupEntranceNtoN.getGroupToEntranceId()));

                        Map<Long, Object> oneAccessGroup = new HashMap();
                        Map<String, Object> personsAndSchedule = new HashMap();
                        List<Map> EditedListofPersons = new ArrayList<Map>(1);

                        for (Person person : ListofPersons) {
                            Map<String, Object> eachPerson = new HashMap();
                            eachPerson.put("Name", person.getPersonId());

                            List<CredentialDto> ListofCred = credentialService.findByPersonId(person.getPersonId());
                            Map<String, List<Object>> personcredentials = new HashMap();
                            for (CredentialDto credentialDto : ListofCred) {
                                if (credentialDto.getIsValid()) {
                                    String credType = credentialDto.getCredType().getCredTypeName();
                                    List<Object> temp = new ArrayList<>();

                                    Map<String, Object> eachCred = new HashMap();

                                    eachCred.put("Value",credentialDto.getCredUid());
                                    eachCred.put("EndDate",credentialDto.getCredTTL().toString().substring(0,10));
                                    eachCred.put("IsPerm",credentialDto.getIsPerm());

                                    if (personcredentials.containsKey(credType)) {
                                        temp = personcredentials.get(credType);
                                        temp.add(eachCred);
                                    } else {

                                        temp.add(eachCred);
                                    }
                                    personcredentials.put(credType, temp);
                                }
                            }

                            eachPerson.put("Credentials", personcredentials);
                            EditedListofPersons.add(eachPerson);
                        }

                        personsAndSchedule.put("Persons", EditedListofPersons);
                        try {
                            personsAndSchedule.put("Schedule", GetAccessGroupScheduleObjectWithTime(ListofSchedule));
                        } catch (Exception e) {
                            throw new Exception ("ERROR WITH Access Group Schedule: " + e);
                        }

                        oneAccessGroup.put(accessGroupEntranceNtoN.getAccessGroup().getAccessGroupId(), personsAndSchedule);

                        accessGroups.add(oneAccessGroup);
                    }}


//                for(User user : listOfUsers) {
//                    List<User> users = new ArrayList<User>(1);
//                    users.add(user);
//                    usersByCountry.put(user.getCountry(), users);
//                }


                    existingentrancedetails.put("AccessGroups", accessGroups);
                    entrance.put("ThirdPartyOptions", existingentrance.getThirdPartyOption());
                    entrance.put("EntranceDetails", existingentrancedetails);
                    entrance.put("isActive", existingentrance.getIsActive());

                    RulesSet.add(entrance);
//                } catch (Exception e) {
//
//                    System.out.println("ERROR "+e);
//                }
                }else{
                    System.out.println(existingentrance);
                }
            }
        System.out.println("SENDING CREDOCCUR TO CONTROLLER IP "+ existingcontroller.getControllerIP());

        try{

            String resourceUrl = "http://"+ existingcontroller.getControllerIP()+":5000/api/credOccur";
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<List> request = new HttpEntity<>(RulesSet);
//            System.out.println(request);
            System.out.println("CHECKPOINT CredOccur");
            ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCodeValue() == 204){
//                ObjectMapper mapper = new ObjectMapper();
//                ControllerConnection connection = mapper.readValue(response.getBody(), ControllerConnection.class);
                return HttpStatus.OK;
            }
            else{
                throw new Exception("API call fail");
            }

            //call entrancename function
            }
        catch (Exception e){
            throw e;
        }

//        }
//        catch(Exception e){
//            System.out.println(e);
//        }
    }

    public ResponseEntity<?> sendEventsManagementToController(Controller controller) throws Exception {
        System.out.println("SENDING EVENTMANAGEMENT TO CONTROLLER IP "+ controller.getControllerIP().toString());
        List<EventsManagement> toSend = controller.getEventsManagements();
        try {
            Set<Long> entranceIds = new HashSet<>();
            for (AuthDevice ad : controller.getAuthDevices()) {
                if (ad.getEntrance() != null)
                    entranceIds.add(ad.getEntrance().getEntranceId());
            }

            List<Entrance> entrances = entranceRepo.findByEntranceIdInAndDeletedFalseAndIsActiveTrue(entranceIds);
            entrances.forEach(ent -> toSend.addAll(ent.getEventsManagements()));
            AtomicBoolean genScheduleError = new AtomicBoolean(false);
            List<EventsManagementPiDto> controllerEms = toSend.stream()
                    .map(em -> {

                                Map<String, Object> schedules = new HashMap<>();

                                for (TriggerSchedules ts :triggerSchedulesRepository.findAllById(em.getTriggerSchedulesid()))
                                    try {

                                        schedules = getScheduleMap(ts.getRrule(),ts.getTimeStart(),
                                                ts.getTimeEnd(),  schedules);
                                    } catch (Exception e) {
                                        genScheduleError.set(true);
                                    }

                                return new EventsManagementPiDto(
                                        em.getEventsManagementId(), em.getEventsManagementName(),
                                        inputEventRepo.findAllById(em.getInputEventsId()),
                                        outputEventRepo.findAllById(em.getOutputActionsId()),
                                        schedules,
                                        em.getEntrance() == null ? null : EventsManagementPiDto.getEntranceId(em),
                                        em.getController() == null ? null : EventsManagementPiDto.getControllerId(em)
                                );
                            }
                    ).collect(Collectors.toList());

            if (genScheduleError.get()) {
                throw new Exception("Schedule generation error");
            }

            String resourceUrl = "http://" + controller.getControllerIP() + ":5000/api/eventActionTriggers";
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<List> request = new HttpEntity<>(controllerEms);
            ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCodeValue() == 204){
//                ObjectMapper mapper = new ObjectMapper();
//                ControllerConnection connection = mapper.readValue(response.getBody(), ControllerConnection.class);
                return ResponseEntity.ok().build();
            }
            else{
                throw new Exception("API call fail");
            }
            //        System.out.println(productCreateResponse.getStatusCode());
        }
        catch(Exception e){
            throw new Exception("An error occurred");
        }
    }

    public void save(Controller existingcontroller) {
        controllerRepository.save(existingcontroller);
    }

    public Boolean IsIPavailable(String ipAddress)
            throws Exception
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

    public Boolean unlockEntrance(Entrance entrance) throws Exception{
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(3000);
        httpRequestFactory.setConnectTimeout(3000);
        httpRequestFactory.setReadTimeout(3000);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        try{
            Optional<Controller> existingController = controllerRepository.findByAuthDevices_Entrance_EntranceIdAndDeletedFalse(
                    entrance.getEntranceId());

            if (existingController.isEmpty()){
                return false;
            }

            String resourceUrl = "http://"+ existingController.get().getControllerIP()+":5000/api/unlock/entrance/"+
                    entrance.getEntranceId();

            ResponseEntity<?> productCreateResponse =
                    restTemplate.exchange(resourceUrl, HttpMethod.GET, null, String.class);
            if (productCreateResponse.getStatusCodeValue() == 200) {
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    public Boolean UpdateUniconIP(FrontendControllerDto newFrontendControllerDto) throws Exception {

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(3000);
        httpRequestFactory.setConnectTimeout(3000);
        httpRequestFactory.setReadTimeout(3000);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        Controller existingController = controllerRepository.findById(newFrontendControllerDto.getControllerId()).get();
        if (newFrontendControllerDto.getControllerIPStatic() == true){
            existingController.setPendingIP(newFrontendControllerDto.getControllerIP());
            save(existingController);
        }
        else{
            existingController.setPendingIP(null);
        }

        String resourceUrl = "http://"+ existingController.getControllerIP()+":5000/api/config";

        Map<String,Object> requestBody = new LinkedHashMap<>();
        requestBody.put("controllerIPStatic",newFrontendControllerDto.getControllerIPStatic());
        requestBody.put("controllerIP",newFrontendControllerDto.getControllerIP());
        requestBody.put("controllerSerialNo",newFrontendControllerDto.getControllerSerialNo());

        HttpEntity<Map> request = new HttpEntity<Map>(requestBody);

        try{

            ResponseEntity<?> productCreateResponse =
                    restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);
            if (productCreateResponse.getStatusCodeValue() == 204) {
                existingController.setControllerIP(existingController.getPendingIP());
                existingController.setPendingIP(null);
                save(existingController);
            }
            return true;
        }
        catch(Exception e){
            if (newFrontendControllerDto.getControllerIPStatic() == false){

                Thread.sleep(8000);
//                entityManager.getEntityManagerFactory().getCache().evictAll();
//                LocalDateTime lastonlinedatetime = controllerRepository.findByControllerSerialNoEqualsAndDeletedIsFalse(newFrontendControllerDto
//                        .getControllerSerialNo()).get().getLastOnline();
//
//                LocalDateTime currentdatetime = LocalDateTime.now(ZoneId.of("GMT+08:00"));
//                //unable to cehck if changes to dhcp is successful
//                System.out.println(lastonlinedatetime);
//                System.out.println(currentdatetime);
//                System.out.println(currentdatetime.minusSeconds(5));
//                System.out.println(lastonlinedatetime.isAfter(currentdatetime.minusSeconds(5)));
//
//                if (lastonlinedatetime.isAfter(currentdatetime.minusSeconds(5))) {
//                    return true;
//                }
                return true;
            }
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

    // return auth method list with auth method and schedule
    public List<Map> GetAuthMethodScheduleObjectWithTime(List<AuthMethodScheduleDto> ListofAuthMethodSchedule) throws Exception {

        List<Map> AuthMethod = new ArrayList<Map>();

        for (AuthMethodScheduleDto authMethodSchedule : ListofAuthMethodSchedule) {
            Map<String, Object> authMethodAndSchedule = new HashMap();
            Boolean authMethodExists = false;

            String rawrrule = authMethodSchedule.getRrule();
            String starttime = authMethodSchedule.getTimeStart();
            String endtime = authMethodSchedule.getTimeEnd();

            // if auth method already exist
            for (Map existingAuthMethodAndSchedule : AuthMethod){
                if ( existingAuthMethodAndSchedule.containsValue(authMethodSchedule.getAuthMethod().getAuthMethodDesc())){
                    ObjectMapper oMapper = new ObjectMapper();
                    // add to existing schedule
                    Map existingSchedule = oMapper.convertValue(existingAuthMethodAndSchedule.get("Schedule"),Map.class);
                    authMethodAndSchedule.put("Schedule", getScheduleMap(rawrrule,starttime,endtime,existingSchedule));
                    authMethodExists =  true;
                    break;
                }
            }

            if (!authMethodExists){
                // add method and schedule
                authMethodAndSchedule.put("Method", authMethodSchedule.getAuthMethod().getAuthMethodDesc());
                authMethodAndSchedule.put("Schedule", getScheduleMap(rawrrule,starttime,endtime,new HashMap<>()));
            }

            AuthMethod.add(authMethodAndSchedule);
        }

        return AuthMethod;
    }

    // takes in a list of entrance schedule and return schedule

    public Map GetEntranceScheduleObjectWithTime(List <EntranceSchedule> exisitngEntranceSchedules) throws Exception {

        Map<String,Object> combinedSchedule = new HashMap<>();

        for ( EntranceSchedule singleEntranceSchedule : exisitngEntranceSchedules)
        {
            String rawrrule = singleEntranceSchedule.getRrule();
            String starttime = singleEntranceSchedule.getTimeStart();
            String endtime = singleEntranceSchedule.getTimeEnd();
            combinedSchedule = getScheduleMap(rawrrule,starttime,endtime,combinedSchedule);
        }
//        System.out.println(combinedSchedule);
        return combinedSchedule;
    }

    public Map GetAccessGroupScheduleObjectWithTime(List <AccessGroupScheduleDto> exisitngAccessGroupSchedules) throws Exception {

        Map<String,Object> combinedSchedule = new HashMap<>();

        for ( AccessGroupScheduleDto singleAccessGroupSchedule : exisitngAccessGroupSchedules) {
            String rawrrule = singleAccessGroupSchedule.getRrule();
            String starttime = singleAccessGroupSchedule.getTimeStart();
            String endtime = singleAccessGroupSchedule.getTimeEnd();
            combinedSchedule = getScheduleMap(rawrrule,starttime,endtime,combinedSchedule);
        }
//        System.out.println(combinedSchedule);
        return combinedSchedule;
    }

    // add to existing schedule and return
    public Map getScheduleMap(String rawrrule, String starttime, String endtime, Map combinedSchedule) throws Exception {
    // add to existing schedule and return {
    //    //            "2022-07-15":[
    //    //                {
    //    //                    "endtime":"23:59",
    //    //                    "starttime":"00:00"
    //    //                },
    //    //                {
    //    //                    "endtime":"12:00",
    //    //                    "starttime":"11:59"
    //    //                }
    //    //            ],
    //    //            "2023-05-30":[
    //    //                {
    //    //                    "endtime":"23:57",
    //    //                    "starttime":"22:56"
    //    //                },
    //    //                {
    //    //                    "endtime":"11:56",
    //    //                    "starttime":"11:55"
    //    //                }
    //    //            ]
    //    //        }

    // iterate through a list of objects ( schedules ), call GetScheduleMap and keep adding to the combined schedule
    // can refer to GetEntranceScheduleObjectWithTime for reference

        String startdatetime = rawrrule.split("\n")[0].split(":")[1].split("T")[0];
        String rrule = rawrrule.split("\n")[1].split(":")[1];

        Integer year = Integer.parseInt(startdatetime.substring(0,4));
        Integer month = Integer.parseInt(startdatetime.substring(4,6));
        Integer day = Integer.parseInt(startdatetime.substring(6,8));

        if (LocalDate.now().getYear() > year){
            year = LocalDate.now().getYear();
        }

        if (LocalDate.now().getMonthValue() > month){
            month = LocalDate.now().getMonthValue();
        }

        if (LocalDate.now().getDayOfMonth() > day){
            day = LocalDate.now().getDayOfMonth();
        }

        //count, dont exceed one year
        //count, exceed one year
        //end date, dont exceed one year
        //count, exceed one year

        if ( rrule.contains("UNTIL=")){
            // contains UNTIL
            int indexOfUntil = rrule.lastIndexOf("UNTIL=");

            try{
                rrule = rrule.substring(0,indexOfUntil+9) + rrule.substring(indexOfUntil+17);
            }
            catch (Exception e){
                rrule = rrule.substring(0,indexOfUntil+9);
            }

        }

        RecurrenceRule rule = new RecurrenceRule(rrule);
        DateTime start = new DateTime(year, month-1 /* 0-based month numbers! */,day);
        RecurrenceRuleIterator it = rule.iterator(start);

        int maxInstances = 100; // limit instances for rules that recur forever

        if ( rrule.contains("FREQ=DAILY")) {
            maxInstances = 365;
        }

        if ( rrule.contains("FREQ=WEEKLY")) {
            maxInstances = 52;
        }

        if ( rrule.contains("FREQ=MONTHLY")) {
            maxInstances = 12 ;
        }

        if ( rrule.contains("FREQ=YEARLY")) {
            maxInstances = 10;
        }

        // daily 365, weekly 52, monthly 12,
        // set start date to today

        // think about how to generate one year worth
        while (it.hasNext() && (!rule.isInfinite() || maxInstances-- > 0))
        {
            DateTime nextInstance = it.nextDateTime();
            // do something with nextInstance
            String formattedDate = Integer.toString(nextInstance.getYear());

            if (((Integer.toString(nextInstance.getMonth() + 1)).length())==2){
                formattedDate += "-"+(nextInstance.getMonth() + 1);
            }
            else{
                formattedDate += "-0"+(nextInstance.getMonth() + 1);
            }

            if (((Integer.toString(nextInstance.getDayOfMonth())).length())==2){
                formattedDate += "-"+nextInstance.getDayOfMonth();
            }
            else{
                formattedDate += "-0"+nextInstance.getDayOfMonth();
            }

            if (combinedSchedule.containsKey(formattedDate)){
                List <Map> listoStartEndTime = (List<Map>) combinedSchedule.get(formattedDate);

                Map<String,Object> singleStartEndTime = new HashMap<>();
                singleStartEndTime.put("starttime",starttime);
                singleStartEndTime.put("endtime",endtime);

                listoStartEndTime.add(singleStartEndTime);

            }
            else{
                List<Map> listoStartEndTime = new ArrayList<Map>(1);
                Map<String,Object> singleStartEndTime = new HashMap<>();
                singleStartEndTime.put("starttime",starttime);
                singleStartEndTime.put("endtime",endtime);
                listoStartEndTime.add(singleStartEndTime);
                combinedSchedule.put(formattedDate,listoStartEndTime);
            }
        }
        return combinedSchedule;
    }

    public void createGenConfigs(Controller c) {
        for (int i = 1; i <= 3; i++) {
            GENConfigs g = new GENConfigs(null, c, "" + i, null);
            genRepo.save(g);
        }
    }
}
