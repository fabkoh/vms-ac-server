//package com.vmsac.vmsacserver.service;
//
//public class temp {
//    public Map<String, Object> getEntranceNameRelationship(Controller existingController) throws Exception {
//        Map<String, Object> jsonbody = new HashMap();
//        jsonbody.put("controllerSerialNo", existingController.getControllerSerialNo());
//
//        try {
//            Long entranceId1 = existingController.getAuthDevices().stream()
//                    .filter(ad -> ad.getAuthDeviceDirection().equals("E1_IN"))
//                    .findFirst()
//                    .get().getEntrance().getEntranceId();
//            jsonbody.put("E1", entranceId1);
//        } catch (Exception e) {
//            jsonbody.put("E1", "");
//        }
//
//        try {
//            Long entranceId2 = existingController.getAuthDevices().stream()
//                    .filter(ad -> ad.getAuthDeviceDirection().equals("E2_IN"))
//                    .findFirst()
//                    .get().getEntrance().getEntranceId();
//            jsonbody.put("E2", entranceId2);
//        } catch (Exception e) {
//            jsonbody.put("E2", "");
//        }
//        return jsonbody;
//    }
//
//
//    public HttpStatus sendEntranceNameRelationship(Long controllerId) throws Exception {
//        Controller existingController = controllerRepository.getById(controllerId);
//        String resourceUrl = getResourceURL(controllerId, "entrance-name");
//        Map<String, Object> jsonBody = getEntranceNameRelationship(existingController);
//        return sendPostRequest(resourceUrl, jsonBody);
//    }
//
//    public HttpStatus generate(Long controllerId) throws Exception {
//        Controller controller = controllerRepository.getById(controllerId);
//        String resourceUrl = getResourceURL(controllerId, "credOccur");
//        List<Object> rulesSet = createRulesSetForController(controller);
//        return sendPostRequest(resourceUrl, rulesSet);
//    }
//
//    private List<Object> createRulesSetForController(Controller controller) throws Exception {
//        List<Object> rulesSet = new ArrayList<>();
//        for (int i = 1; i <= 2; i++) {
//            Map<String, Object> entranceData = processEntrance(controller, "E" + i + "_IN");
//            if (entranceData != null) {
//                rulesSet.add(entranceData);
//            }
//        }
//        return rulesSet;
//    }
//
//    private Map<String, Object> processEntrance(Controller controller, String direction) throws Exception {
//        Optional<AuthDevice> authDevice = controller.getAuthDevices().stream()
//                .filter(ad -> ad.getAuthDeviceDirection().equals(direction))
//                .findFirst();
//
//        if (authDevice.isPresent() && authDevice.get().getEntrance() != null && authDevice.get().getEntrance().getIsActive()) {
//            Entrance entrance = authDevice.get().getEntrance();
//            return buildEntranceData(entrance);
//        }
//        return null;
//    }
//
//    private Map<String, Object> buildEntranceData(Entrance entrance) throws Exception {
//        Map<String, Object> entranceData = new HashMap<>();
//        entranceData.put("Entrance", entrance.getEntranceId());
//        entranceData.put("EntranceSchedule", getEntranceScheduleObjectWithTime(entranceScheduleRepository
//                .findByEntranceIdAndDeletedFalseAndIsActiveTrue(entrance.getEntranceId())));
//        entranceData.put("EntranceDetails", getEntranceDetails(entrance));
//        entranceData.put("ThirdPartyOptions", entrance.getThirdPartyOption());
//        entranceData.put("isActive", entrance.getIsActive());
//
//        return entranceData;
//    }
//
//    private Map<String, Object> getEntranceDetails(Entrance existingentrance) throws Exception {
//        Map<String, Object> entranceDetails = new HashMap<>();
//        entranceDetails.put("Antipassback", "No");
//        entranceDetails.put("Zone", "ZoneId");
//
//        Map<String, Object> authDevices = getAuthDevicesDetails(existingentrance);
//        entranceDetails.put("AuthenticationDevices", authDevices);
//
//        List<Map> accessGroups = getAccessGroupsDetails(existingentrance);
//        entranceDetails.put("AccessGroups", accessGroups);
//
//        return entranceDetails;
//    }
//
//    private Map<String, Object> getAuthDevicesDetails(Entrance entrance) throws Exception {
//        Map<String, Object> authDevices = new HashMap<>();
//
//        // Refactor the code to handle IN and OUT devices in a loop or a separate method
//        // Here's an example with a loop:
//        for (String direction : Arrays.asList("IN", "OUT")) {
//            AuthDevice device = authDeviceRepository.findByEntrance_EntranceIdIsAndAuthDeviceDirectionContains(entrance.getEntranceId(), direction);
//            if (device != null) {
//                Map<String, Object> deviceDetails = getDeviceDetails(device);
//                authDevices.put(direction, deviceDetails);
//            }
//        }
//
//        return authDevices;
//    }
//
//    private Map<String, Object> getDeviceDetails(AuthDevice device) throws Exception {
//        Map<String, Object> deviceDetails = new HashMap<>();
//        String MASTERPASSWORD = "666666";
//
//        deviceDetails.put("Masterpassword", device.getMasterpin() ? MASTERPASSWORD : false);
//        deviceDetails.put("Direction", device.getAuthDeviceDirection().substring(3));
//        deviceDetails.put("defaultAuthMethod", device.getDefaultAuthMethod().getAuthMethodDesc());
//        deviceDetails.put("AuthMethod", GetAuthMethodScheduleObjectWithTime(authMethodScheduleService.findByDeviceIdAndIsActive(device.getAuthDeviceId())));
//
//        return deviceDetails;
//    }
//
//    private List<Map> getAccessGroupsDetails(Entrance entrance) throws Exception {
//        List<Map> accessGroups = new ArrayList<>();
//
//        for (AccessGroupEntranceNtoN accessGroupEntranceNtoN : accessGroupEntranceNtoNRepository.findAllByEntranceEntranceIdAndDeletedFalse(entrance.getEntranceId())) {
//            if (accessGroupEntranceNtoN.getAccessGroup().getIsActive()) {
//                Map<Long, Object> groupDetails = getOneAccessGroupDetails(accessGroupEntranceNtoN);
//                accessGroups.add(groupDetails);
//            }
//        }
//
//        return accessGroups;
//    }
//
//    private Map<Long, Object> getOneAccessGroupDetails(AccessGroupEntranceNtoN accessGroupEntranceNtoN) throws Exception {
//        Map<Long, Object> groupDetails = new HashMap<>();
//        List<Person> listOfPersons = personService.findByAccGrpId(accessGroupEntranceNtoN.getAccessGroup().getAccessGroupId(), false);
//        List<AccessGroupScheduleDto> listOfSchedule = accessGroupScheduleService.findAllByGroupToEntranceIdInAndIsActiveTrue(Collections.singletonList(accessGroupEntranceNtoN.getGroupToEntranceId()));
//
//        List<Map> editedListOfPersons = getEditedListOfPersons(listOfPersons);
//        Map<String, Object> personsAndSchedule = new HashMap<>();
//        personsAndSchedule.put("Persons", editedListOfPersons);
//        personsAndSchedule.put("Schedule", GetAccessGroupScheduleObjectWithTime(listOfSchedule));
//
//        groupDetails.put(accessGroupEntranceNtoN.getAccessGroup().getAccessGroupId(), personsAndSchedule);
//
//        return groupDetails;
//    }
//
//    private List<Map> getEditedListOfPersons(List<Person> listOfPersons) {
//        List<Map> editedListOfPersons = new ArrayList<>();
//
//        for (Person person : listOfPersons) {
//            Map<String, Object> personDetails = getPersonDetails(person);
//            editedListOfPersons.add(personDetails);
//        }
//
//        return editedListOfPersons;
//    }
//
//    private Map<String, Object> getPersonDetails(Person person) {
//        Map<String, Object> personDetails = new HashMap<>();
//        personDetails.put("Name", person.getPersonId());
//
//        Map<String, List<Object>> personCredentials = getPersonCredentials(person);
//        personDetails.put("Credentials", personCredentials);
//
//        return personDetails;
//    }
//
//    private Map<String, List<Object>> getPersonCredentials(Person person) {
//        Map<String, List<Object>> personCredentials = new HashMap<>();
//
//        for (CredentialDto credentialDto : credentialService.findByPersonId(person.getPersonId())) {
//            if (credentialDto.getIsValid()) {
//                addCredentialToPersonCredentials(credentialDto, personCredentials);
//            }
//        }
//
//        return personCredentials;
//    }
//
//    private void addCredentialToPersonCredentials(CredentialDto credentialDto, Map<String, List<Object>> personCredentials) {
//        String credType = credentialDto.getCredType().getCredTypeName();
//        Map<String, Object> credentialDetails = new HashMap<>();
//        credentialDetails.put("Value", credentialDto.getCredUid());
//        credentialDetails.put("EndDate", credentialDto.getCredTTL().toString().substring(0, 10));
//        credentialDetails.put("IsPerm", credentialDto.getIsPerm());
//
//        if (!personCredentials.containsKey(credType)) {
//            personCredentials.put(credType, new ArrayList<>());
//        }
//        personCredentials.get(credType).add(credentialDetails);
//    }
//
//
//    // return auth method list with auth method and schedule
//    public List<Map> GetAuthMethodScheduleObjectWithTime(List<AuthMethodScheduleDto> ListofAuthMethodSchedule) throws Exception {
//
//        List<Map> AuthMethod = new ArrayList<Map>();
//
//        for (AuthMethodScheduleDto authMethodSchedule : ListofAuthMethodSchedule) {
//            Map<String, Object> authMethodAndSchedule = new HashMap();
//            Boolean authMethodExists = false;
//
//            String rawrrule = authMethodSchedule.getRrule();
//            String starttime = authMethodSchedule.getTimeStart();
//            String endtime = authMethodSchedule.getTimeEnd();
//
//            // if auth method already exist
//            for (Map existingAuthMethodAndSchedule : AuthMethod){
//                if ( existingAuthMethodAndSchedule.containsValue(authMethodSchedule.getAuthMethod().getAuthMethodDesc())){
//                    ObjectMapper oMapper = new ObjectMapper();
//                    // add to existing schedule
//                    Map existingSchedule = oMapper.convertValue(existingAuthMethodAndSchedule.get("Schedule"),Map.class);
//                    authMethodAndSchedule.put("Schedule", getScheduleMap(rawrrule,starttime,endtime,existingSchedule));
//                    authMethodExists =  true;
//                    break;
//                }
//            }
//
//            if (!authMethodExists){
//                // add method and schedule
//                authMethodAndSchedule.put("Method", authMethodSchedule.getAuthMethod().getAuthMethodDesc());
//                authMethodAndSchedule.put("Schedule", getScheduleMap(rawrrule,starttime,endtime,new HashMap<>()));
//            }
//
//            AuthMethod.add(authMethodAndSchedule);
//        }
//
//        return AuthMethod;
//    }
//
//    // takes in a list of entrance schedule and return schedule
//
//    public Map getEntranceScheduleObjectWithTime(List <EntranceSchedule> exisitngEntranceSchedules) throws Exception {
//
//        Map<String,Object> combinedSchedule = new HashMap<>();
//
//        for ( EntranceSchedule singleEntranceSchedule : exisitngEntranceSchedules)
//        {
//            String rawrrule = singleEntranceSchedule.getRrule();
//            String starttime = singleEntranceSchedule.getTimeStart();
//            String endtime = singleEntranceSchedule.getTimeEnd();
//            combinedSchedule = getScheduleMap(rawrrule,starttime,endtime,combinedSchedule);
//        }
////        System.out.println(combinedSchedule);
//        return combinedSchedule;
//    }
//
//    public Map GetAccessGroupScheduleObjectWithTime(List <AccessGroupScheduleDto> exisitngAccessGroupSchedules) throws Exception {
//
//        Map<String,Object> combinedSchedule = new HashMap<>();
//
//        for ( AccessGroupScheduleDto singleAccessGroupSchedule : exisitngAccessGroupSchedules) {
//            String rawrrule = singleAccessGroupSchedule.getRrule();
//            String starttime = singleAccessGroupSchedule.getTimeStart();
//            String endtime = singleAccessGroupSchedule.getTimeEnd();
//            combinedSchedule = getScheduleMap(rawrrule,starttime,endtime,combinedSchedule);
//        }
////        System.out.println(combinedSchedule);
//        return combinedSchedule;
//    }
//
//    // add to existing schedule and return
//    public Map getScheduleMap(String rawrrule, String starttime, String endtime, Map combinedSchedule) throws Exception {
//
//        Map<String,Object> rruleStartTimeObject = new HashMap<>();
//        rruleStartTimeObject.put("rrule", rawrrule);
//        rruleStartTimeObject.put("starttime",starttime);
//        rruleStartTimeObject.put("endtime",endtime);
//
//        return rruleStartTimeObject;
//        // add to existing schedule and return {
//        //    //            "2022-07-15":[
//        //    //                {
//        //    //                    "endtime":"23:59",
//        //    //                    "starttime":"00:00"
//        //    //                },
//        //    //                {
//        //    //                    "endtime":"12:00",
//        //    //                    "starttime":"11:59"
//        //    //                }
//        //    //            ],
//        //    //            "2023-05-30":[
//        //    //                {
//        //    //                    "endtime":"23:57",
//        //    //                    "starttime":"22:56"
//        //    //                },
//        //    //                {
//        //    //                    "endtime":"11:56",
//        //    //                    "starttime":"11:55"
//        //    //                }
//        //    //            ]
//        //    //        }
//
//        // iterate through a list of objects ( schedules ), call GetScheduleMap and keep adding to the combined schedule
//        // can refer to GetEntranceScheduleObjectWithTime for reference
//
////        String startdatetime = rawrrule.split("\n")[0].split(":")[1].split("T")[0];
////        String rrule = rawrrule.split("\n")[1].split(":")[1];
////
////        Integer year = Integer.parseInt(startdatetime.substring(0,4));
////        Integer month = Integer.parseInt(startdatetime.substring(4,6));
////        Integer day = Integer.parseInt(startdatetime.substring(6,8));
////
////        if (LocalDate.now().getYear() > year){
////            year = LocalDate.now().getYear();
////        }
////
////        if (LocalDate.now().getMonthValue() > month){
////            month = LocalDate.now().getMonthValue();
////        }
////
////        if (LocalDate.now().getDayOfMonth() > day){
////            day = LocalDate.now().getDayOfMonth();
////        }
////
////        //count, dont exceed one year
////        //count, exceed one year
////        //end date, dont exceed one year
////        //count, exceed one year
////
////        if ( rrule.contains("UNTIL=")){
////            // contains UNTIL
////            int indexOfUntil = rrule.lastIndexOf("UNTIL=");
////
////            try{
////                rrule = rrule.substring(0,indexOfUntil+9) + rrule.substring(indexOfUntil+17);
////            }
////            catch (Exception e){
////                rrule = rrule.substring(0,indexOfUntil+9);
////            }
////
////        }
////
////        RecurrenceRule rule = new RecurrenceRule(rrule);
////        DateTime start = new DateTime(year, month-1 /* 0-based month numbers! */,day);
////        RecurrenceRuleIterator it = rule.iterator(start);
////
////        int maxInstances = 100; // limit instances for rules that recur forever
////
////        if ( rrule.contains("FREQ=DAILY")) {
////            maxInstances = 365;
////        }
////
////        if ( rrule.contains("FREQ=WEEKLY")) {
////            maxInstances = 52;
////        }
////
////        if ( rrule.contains("FREQ=MONTHLY")) {
////            maxInstances = 12 ;
////        }
////
////        if ( rrule.contains("FREQ=YEARLY")) {
////            maxInstances = 10;
////        }
////
////        // daily 365, weekly 52, monthly 12,
////        // set start date to today
////
////        // think about how to generate one year worth
////        while (it.hasNext() && (!rule.isInfinite() || maxInstances-- > 0))
////        {
////            DateTime nextInstance = it.nextDateTime();
////            // do something with nextInstance
////            String formattedDate = Integer.toString(nextInstance.getYear());
////
////            if (((Integer.toString(nextInstance.getMonth() + 1)).length())==2){
////                formattedDate += "-"+(nextInstance.getMonth() + 1);
////            }
////            else{
////                formattedDate += "-0"+(nextInstance.getMonth() + 1);
////            }
////
////            if (((Integer.toString(nextInstance.getDayOfMonth())).length())==2){
////                formattedDate += "-"+nextInstance.getDayOfMonth();
////            }
////            else{
////                formattedDate += "-0"+nextInstance.getDayOfMonth();
////            }
////
////            if (combinedSchedule.containsKey(formattedDate)){
////                List <Map> listoStartEndTime = (List<Map>) combinedSchedule.get(formattedDate);
////
////                Map<String,Object> singleStartEndTime = new HashMap<>();
////                singleStartEndTime.put("starttime",starttime);
////                singleStartEndTime.put("endtime",endtime);
////
////                listoStartEndTime.add(singleStartEndTime);
////
////            }
////            else{
////                List<Map> listoStartEndTime = new ArrayList<Map>(1);
////                Map<String,Object> singleStartEndTime = new HashMap<>();
////                singleStartEndTime.put("starttime",starttime);
////                singleStartEndTime.put("endtime",endtime);
////                listoStartEndTime.add(singleStartEndTime);
////                combinedSchedule.put(formattedDate,listoStartEndTime);
////            }
////        }
////        return combinedSchedule;
//    }"
//}
