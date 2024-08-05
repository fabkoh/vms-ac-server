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
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class ControllerService {

    String MASTERPASSWORD = "666666";
    String pinAssignment = "{'E1_IN_D0': '14'}";
    String settingsConfig = "testsettings";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    public ControllerService() {
        this.restTemplate = new RestTemplate();
    }

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
    private PersonRepository personRepository;

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
            httpRequestFactory.setConnectionRequestTimeout(5000);
            httpRequestFactory.setConnectTimeout(5000);
            httpRequestFactory.setReadTimeout(5000);

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

    private HttpStatus sendPostRequest(String url, Map<String, Object> body) throws Exception {
        HttpEntity<Map> request = new HttpEntity<>(body);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        if (response.getStatusCode().value() == 200) {
//            handleResponse(response);
            return HttpStatus.OK;
        } else {
            throw new Exception("Invalid JSON format in response");
        }
    }

    private HttpStatus sendPostRequest(String url, List<?> body) throws Exception {
        HttpEntity<List<?>> request = new HttpEntity<>(body);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        if (response.getStatusCode().value() == 200) {
//            handleResponse(response);
            return HttpStatus.OK;
        } else {
            throw new Exception("Invalid JSON format in response");
        }
    }

    private boolean isJsonValid(String jsonInString) {
        try {
            System.out.println(jsonInString);
            objectMapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void handleResponse(ResponseEntity<String> response) throws Exception {
        if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new Exception("API call failed with status: " + response.getStatusCode());
        }
    }


    public String getResourceURL(Long controllerId, String URLEndPoint) throws Exception {
        Controller existingController = controllerRepository.getById(controllerId);
        String IPAddress = existingController.getControllerIP();
        String resourceUrl = "http://"+ IPAddress +":5000/api/" + URLEndPoint;
        return resourceUrl;
    }

    public Map<String, Object> getEntranceNameRelationship(Controller existingController) throws Exception {
        Map<String, Object> jsonbody = new HashMap<>();
        jsonbody.put("controllerSerialNo", existingController.getControllerSerialNo());

        try {
            Long entranceId1 = existingController.getAuthDevices().stream()
                    .filter(ad -> ad.getAuthDeviceDirection().equals("E1_IN"))
                    .findFirst()
                    .get().getEntrance().getEntranceId();
            jsonbody.put("E1", entranceId1);
        } catch (Exception e) {
            jsonbody.put("E1", "");
        }

        try {
            Long entranceId2 = existingController.getAuthDevices().stream()
                    .filter(ad -> ad.getAuthDeviceDirection().equals("E2_IN"))
                    .findFirst()
                    .get().getEntrance().getEntranceId();
            jsonbody.put("E2", entranceId2);
        } catch (Exception e) {
            jsonbody.put("E2", "");
        }
        return jsonbody;
    }

    public HttpStatus sendEntranceNameRelationship(Long controllerId) throws Exception {
        Controller existingController = controllerRepository.getById(controllerId);
        String resourceUrl = getResourceURL(controllerId, "entrance-name");
        Map<String, Object> jsonBody = getEntranceNameRelationship(existingController);
        return sendPostRequest(resourceUrl, jsonBody);
    }

    public HttpStatus generate(Long controllerId) throws Exception {
        Controller controller = controllerRepository.getById(controllerId);
        String resourceUrl = getResourceURL(controllerId, "credOccur");
        Map<String, Object> jsonDocument = createJsonDocument(controller);
        return sendPostRequest(resourceUrl, jsonDocument);
    }

    private Map<String, Object> createJsonDocument(Controller controller) throws Exception {
        Map<String, Object> jsonDocument = new HashMap<>();
        List<Object> entrances = createEntrancesList(controller);
        Map<String, Object> credentialLookup = createCredentialLookup(controller);

        jsonDocument.put("Entrances", entrances);
        jsonDocument.put("CredentialLookup", credentialLookup);
        return jsonDocument;
    }

    private List<Object> createEntrancesList(Controller controller) throws Exception {
        List<Object> entrancesList = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Map<String, Object> entranceData = processEntrance(controller, "E" + i + "_IN");
            if (entranceData != null) {
                entrancesList.add(entranceData);
            }
        }
        return entrancesList;
    }

    private Map<String, Object> processEntrance(Controller controller, String direction) throws Exception {
        Optional<AuthDevice> authDevice = controller.getAuthDevices().stream()
                .filter(ad -> ad.getAuthDeviceDirection().equals(direction))
                .findFirst();

        if (authDevice.isPresent() && authDevice.get().getEntrance() != null && authDevice.get().getEntrance().getIsActive()) {
            Entrance entrance = authDevice.get().getEntrance();
            return buildEntranceData(entrance);
        }
        return null;
    }

    private Map<String, Object> buildEntranceData(Entrance entrance) throws Exception {
        Map<String, Object> entranceData = new HashMap<>();
        entranceData.put("Entrance", entrance.getEntranceId());
        entranceData.put("EntranceSchedule", getEntranceScheduleObjectWithTime(entranceScheduleRepository
                .findByEntranceIdAndDeletedFalseAndIsActiveTrue(entrance.getEntranceId())));
        entranceData.put("EntranceDetails", getEntranceDetails(entrance));
        entranceData.put("ThirdPartyOptions", entrance.getThirdPartyOption());
        entranceData.put("isActive", entrance.getIsActive());

        return entranceData;
    }

    private Map<String, Object> getEntranceDetails(Entrance existingentrance) throws Exception {
        Map<String, Object> entranceDetails = new HashMap<>();
        entranceDetails.put("Antipassback", "No");
        entranceDetails.put("Zone", "ZoneId");

        Map<String, Object> authDevices = getAuthDevicesDetails(existingentrance);
        entranceDetails.put("AuthenticationDevices", authDevices);

        List<Map<String, Object>> accessGroups = getAccessGroupsDetails(existingentrance);
        entranceDetails.put("AccessGroups", accessGroups);

        return entranceDetails;
    }

    private Map<String, Object> getAuthDevicesDetails(Entrance entrance) throws Exception {
        Map<String, Object> authDevices = new HashMap<>();

        for (String direction : Arrays.asList("IN", "OUT")) {
            AuthDevice device = authDeviceRepository.findByEntrance_EntranceIdIsAndAuthDeviceDirectionContains(entrance.getEntranceId(), direction);
            if (device != null) {
                Map<String, Object> deviceDetails = getDeviceDetails(device);
                authDevices.put(direction, deviceDetails);
            }
        }

        return authDevices;
    }

    private Map<String, Object> getDeviceDetails(AuthDevice device) throws Exception {
        Map<String, Object> deviceDetails = new HashMap<>();

        deviceDetails.put("Masterpassword", device.getMasterpin() ? MASTERPASSWORD : false);
        deviceDetails.put("Direction", device.getAuthDeviceDirection().substring(3));
        deviceDetails.put("defaultAuthMethod", device.getDefaultAuthMethod().getAuthMethodDesc());
        deviceDetails.put("AuthMethod", GetAuthMethodScheduleObjectWithTime(authMethodScheduleService.findByDeviceIdAndIsActive(device.getAuthDeviceId())));

        return deviceDetails;
    }

    private List<Map<String, Object>> getAccessGroupsDetails(Entrance entrance) throws Exception {
        List<Map<String, Object>> accessGroups = new ArrayList<>();

        for (AccessGroupEntranceNtoN accessGroupEntranceNtoN : accessGroupEntranceNtoNRepository.findAllByEntranceEntranceIdAndDeletedFalse(entrance.getEntranceId())) {
            if (accessGroupEntranceNtoN.getAccessGroup().getIsActive()) {
                Map<String, Object> groupDetails = getOneAccessGroupDetails(accessGroupEntranceNtoN);
                accessGroups.add(groupDetails);
            }
        }

        return accessGroups;
    }

    private Map<String, Object> getOneAccessGroupDetails(AccessGroupEntranceNtoN accessGroupEntranceNtoN) throws Exception {
        Map<String, Object> groupDetails = new HashMap<>();
        groupDetails.put("GroupId", accessGroupEntranceNtoN.getAccessGroup().getAccessGroupId());

        List<Person> listOfPersons = personService.findByAccGrpId(accessGroupEntranceNtoN.getAccessGroup().getAccessGroupId(), false);
        List<Long> personIds = listOfPersons.stream().map(Person::getPersonId).collect(Collectors.toList());
        groupDetails.put("Persons", personIds);

        List<AccessGroupScheduleDto> listOfSchedule = accessGroupScheduleService.findAllByGroupToEntranceIdInAndIsActiveTrue(Collections.singletonList(accessGroupEntranceNtoN.getGroupToEntranceId()));
        groupDetails.put("Schedule", GetAccessGroupScheduleObjectWithTime(listOfSchedule));

        return groupDetails;
    }

    private Map<String, Object> createCredentialLookup(Controller controller) throws Exception {
        Map<String, Object> credentialLookup = new HashMap<>();

        List<Person> allPersons = controller.getAssignedEntrances().stream()
                .flatMap(entrance -> entrance.getAssignedAccessGroup(accessGroupEntranceNtoNRepository).stream())
                .flatMap(accessGroup -> accessGroup.getAssignedPersons(personRepository).stream())
                .collect(Collectors.toList());

        for (Person person : allPersons) {
            for (CredentialDto credentialDto : credentialService.findByPersonId(person.getPersonId())) {
                if (credentialDto.getIsValid()) {
                    addCredentialToLookup(credentialDto, credentialLookup, person.getPersonId(), person.getAccessGroup().getAccessGroupId());
                }
            }
        }

        return credentialLookup;
    }

    private void addCredentialToLookup(CredentialDto credentialDto, Map<String, Object> credentialLookup, Long personId, Long accessGroupId) {
        Map<String, Object> credentialDetails = new HashMap<>();
        credentialDetails.put("PersonId", personId);
        credentialDetails.put("IsPerm", credentialDto.getIsPerm());
        credentialDetails.put("EndDate", credentialDto.getCredTTL().toString().substring(0, 10));
        credentialDetails.put("AccessGroup", accessGroupId);

        credentialLookup.put(credentialDto.getCredUid(), credentialDetails);
    }

    public List<Map<String, Object>> GetAuthMethodScheduleObjectWithTime(List<AuthMethodScheduleDto> ListofAuthMethodSchedule) throws Exception {
        List<Map<String, Object>> AuthMethod = new ArrayList<>();

        for (AuthMethodScheduleDto authMethodSchedule : ListofAuthMethodSchedule) {
            Map<String, Object> authMethodAndSchedule = new HashMap<>();
            Boolean authMethodExists = false;

            String rawrrule = authMethodSchedule.getRrule();
            String starttime = authMethodSchedule.getTimeStart();
            String endtime = authMethodSchedule.getTimeEnd();

            for (Map<String, Object> existingAuthMethodAndSchedule : AuthMethod) {
                if (existingAuthMethodAndSchedule.containsValue(authMethodSchedule.getAuthMethod().getAuthMethodDesc())) {
                    ObjectMapper oMapper = new ObjectMapper();
                    Map<String, Object> existingSchedule = oMapper.convertValue(existingAuthMethodAndSchedule.get("Schedule"), Map.class);
                    authMethodAndSchedule.put("Schedule", getScheduleMap(rawrrule, starttime, endtime, existingSchedule));
                    authMethodExists = true;
                    break;
                }
            }

            if (!authMethodExists) {
                authMethodAndSchedule.put("Method", authMethodSchedule.getAuthMethod().getAuthMethodDesc());
                authMethodAndSchedule.put("Schedule", getScheduleMap(rawrrule, starttime, endtime, new HashMap<>()));
            }

            AuthMethod.add(authMethodAndSchedule);
        }

        return AuthMethod;
    }

    public Map<String, Object> getEntranceScheduleObjectWithTime(List<EntranceSchedule> exisitngEntranceSchedules) throws Exception {
        Map<String, Object> combinedSchedule = new HashMap<>();

        for (EntranceSchedule singleEntranceSchedule : exisitngEntranceSchedules) {
            String rawrrule = singleEntranceSchedule.getRrule();
            String starttime = singleEntranceSchedule.getTimeStart();
            String endtime = singleEntranceSchedule.getTimeEnd();
            combinedSchedule = getScheduleMap(rawrrule, starttime, endtime, combinedSchedule);
        }

        return combinedSchedule;
    }

    public Map<String, Object> GetAccessGroupScheduleObjectWithTime(List<AccessGroupScheduleDto> exisitngAccessGroupSchedules) throws Exception {
        Map<String, Object> combinedSchedule = new HashMap<>();

        for (AccessGroupScheduleDto singleAccessGroupSchedule : exisitngAccessGroupSchedules) {
            String rawrrule = singleAccessGroupSchedule.getRrule();
            String starttime = singleAccessGroupSchedule.getTimeStart();
            String endtime = singleAccessGroupSchedule.getTimeEnd();
            combinedSchedule = getScheduleMap(rawrrule, starttime, endtime, combinedSchedule);
        }

        return combinedSchedule;
    }

    public Map<String, Object> getScheduleMap(String rawrrule, String starttime, String endtime, Map<String, Object> combinedSchedule) {
        Map<String, Object> rruleStartTimeObject = new HashMap<>();
        rruleStartTimeObject.put("rrule", rawrrule);
        rruleStartTimeObject.put("starttime", starttime);
        rruleStartTimeObject.put("endtime", endtime);

        return rruleStartTimeObject;
    }

    public void createGenConfigs(Controller c) {
        for (int i = 1; i <= 3; i++) {
            GENConfigs g = new GENConfigs(null, c, "" + i, null);
            genRepo.save(g);
        }
    }

    public HttpStatus sendEventsManagementToController(Long controllerId) throws Exception {
        Controller controller = controllerRepository.getById(controllerId);
        String resourceUrl = getResourceURL(controllerId, "eventActionTriggers");
        List<EventsManagementPiDto> eventsManagementData = createEventsManagementData(controller);
        return sendPostRequest(resourceUrl, eventsManagementData);
    }

    private List<EventsManagementPiDto> createEventsManagementData(Controller controller) throws Exception {
        List<EventsManagement> eventsManagements = getEventsManagementsForController(controller);
        List<EventsManagementPiDto> controllerEms = new ArrayList<>();

        for (EventsManagement em : eventsManagements) {
            EventsManagementPiDto emDto = convertToEventsManagementPiDto(em);
            controllerEms.add(emDto);
        }

        return controllerEms;
    }

    private List<EventsManagement> getEventsManagementsForController(Controller controller) {
        List<EventsManagement> eventsManagements = new ArrayList<>(controller.getEventsManagements());
        Set<Long> entranceIds = controller.getAuthDevices().stream()
                .map(AuthDevice::getEntrance)
                .filter(Objects::nonNull)
                .map(Entrance::getEntranceId)
                .collect(Collectors.toSet());

        List<Entrance> entrances = entranceRepo.findByEntranceIdInAndDeletedFalseAndIsActiveTrue(entranceIds);
        entrances.forEach(entrance -> eventsManagements.addAll(entrance.getEventsManagements()));

        return eventsManagements;
    }

    private EventsManagementPiDto convertToEventsManagementPiDto(EventsManagement em) throws Exception {
        Map<String, Object> schedules = generateSchedulesForEventManagement(em);
        // Constructing the DTO with all necessary data
        return new EventsManagementPiDto(
                em.getEventsManagementId(),
                em.getEventsManagementName(),
                inputEventRepo.findAllById(em.getInputEventsId()),
                outputEventRepo.findAllById(em.getOutputActionsId()),
                schedules,
                em.getEntrance() != null ? EventsManagementPiDto.getEntranceId(em) : null,
                em.getController() != null ? EventsManagementPiDto.getControllerId(em) : null
        );
    }

    private Map<String, Object> generateSchedulesForEventManagement(EventsManagement em) throws Exception {
        Map<String, Object> schedules = new HashMap<>();
        for (TriggerSchedules ts : triggerSchedulesRepository.findAllById(em.getTriggerSchedulesid())) {
            schedules = getScheduleMap(ts.getRrule(), ts.getTimeStart(), ts.getTimeEnd(), schedules);
        }
        return schedules;
    }

//    public ResponseEntity<?> sendEventsManagementToController(Controller controller) throws Exception {
//        System.out.println("SENDING EVENTMANAGEMENT TO CONTROLLER IP "+ controller.getControllerIP().toString());
//        List<EventsManagement> toSend = controller.getEventsManagements();
//        try {
//            Set<Long> entranceIds = new HashSet<>();
//            for (AuthDevice ad : controller.getAuthDevices()) {
//                if (ad.getEntrance() != null)
//                    entranceIds.add(ad.getEntrance().getEntranceId());
//            }
//
//            List<Entrance> entrances = entranceRepo.findByEntranceIdInAndDeletedFalseAndIsActiveTrue(entranceIds);
//            entrances.forEach(ent -> toSend.addAll(ent.getEventsManagements()));
//            AtomicBoolean genScheduleError = new AtomicBoolean(false);
//            List<EventsManagementPiDto> controllerEms = toSend.stream()
//                    .map(em -> {
//
//                                Map<String, Object> schedules = new HashMap<>();
//
//                                for (TriggerSchedules ts :triggerSchedulesRepository.findAllById(em.getTriggerSchedulesid()))
//                                    try {
//
//                                        schedules = getScheduleMap(ts.getRrule(),ts.getTimeStart(),
//                                                ts.getTimeEnd(),  schedules);
//                                    } catch (Exception e) {
//                                        genScheduleError.set(true);
//                                    }
//
//                                return new EventsManagementPiDto(
//                                        em.getEventsManagementId(), em.getEventsManagementName(),
//                                        inputEventRepo.findAllById(em.getInputEventsId()),
//                                        outputEventRepo.findAllById(em.getOutputActionsId()),
//                                        schedules,
//                                        em.getEntrance() == null ? null : EventsManagementPiDto.getEntranceId(em),
//                                        em.getController() == null ? null : EventsManagementPiDto.getControllerId(em)
//                                );
//                            }
//                    ).collect(Collectors.toList());
//
//            if (genScheduleError.get()) {
//                throw new Exception("Schedule generation error");
//            }
//
//            String resourceUrl = "http://" + controller.getControllerIP() + ":5000/api/eventActionTriggers";
//            RestTemplate restTemplate = new RestTemplate();
//            HttpEntity<List> request = new HttpEntity<>(controllerEms);
//            ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.POST, request, String.class);
//
//            if (response.getStatusCodeValue() == 204){
////                ObjectMapper mapper = new ObjectMapper();
////                ControllerConnection connection = mapper.readValue(response.getBody(), ControllerConnection.class);
//                return ResponseEntity.ok().build();
//            }
//            else{
//                throw new Exception("API call fail");
//            }
//            //        System.out.println(productCreateResponse.getStatusCode());
//        }
//        catch(Exception e){
//            throw new Exception("An error occurred");
//        }
//    }

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



    public ResponseEntity<?> getPiPropertyFromController(Controller controller) throws Exception {
        System.out.println("GETTING PIPROPERTY FROM CONTROLLER IP " + controller.getControllerIP());
        try {
            String resourceUrl = "http://" + controller.getControllerIP() + ":5000/api/piProperty";
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> request = new HttpEntity<String>("");
            return restTemplate.exchange(resourceUrl, HttpMethod.GET, request, String.class);
        }
        catch(Exception e) {
            throw new Exception("An error occurred");
        }
    }
}
