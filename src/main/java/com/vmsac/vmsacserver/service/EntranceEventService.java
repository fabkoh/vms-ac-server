package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.repository.AuthMethodRepository;
import com.vmsac.vmsacserver.repository.EntranceEventRepository;
import com.vmsac.vmsacserver.repository.EntranceEventTypeRepository;
import com.vmsac.vmsacserver.util.DateTimeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EntranceEventService {

    @Autowired
    private EntranceEventRepository entranceEventRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private EntranceService entranceService;

    @Autowired
    private AccessGroupService accessGroupService;

    @Autowired
    private ControllerService controllerService;

    @Autowired
    private EntranceEventTypeRepository entranceEventTypeRepository;

    @Autowired
    private AuthMethodRepository authMethodRepository;

    @Autowired
    private DateTimeParser dateTimeParser;

    public Boolean createEvents(List<EntranceEventDto> newEventDtos){
        // iterate over the whole list
        // check if it exists in db
        // check if linked columns are valid
        // record and save
        Boolean success = true;

        try{
        for (EntranceEventDto e : newEventDtos){

            LocalDateTime eventTime = dateTimeParser.toLocalDateTime(e.getEventTime());

            if (entranceEventRepository.existsByEventTimeEqualsAndEntrance_EntranceIdEqualsAndEntranceEventType_ActionTypeId
                    (eventTime, e.getEntranceId(), e.getEventActionTypeId())){
                continue;
            }

            EntranceEvent toSave = new EntranceEvent();
            System.out.println("1");
            toSave.setDirection(e.getDirection());
            toSave.setEventTime(eventTime);
            toSave.setDeleted(false);

            System.out.println("2");
            Optional<Person> optionalPerson = personService.findByIdInUse(e.getPersonId());
            if (optionalPerson.isEmpty()){
                toSave.setPerson(null);
            }
            else{
                toSave.setPerson(optionalPerson.get());
            }

            System.out.println("3");
            Optional<Entrance> optionalEntrance = entranceService.findById(e.getEntranceId());
            if (optionalEntrance.isEmpty()){
                toSave.setEntrance(null);
            }
            else{
                toSave.setEntrance(optionalEntrance.get());
            }

            System.out.println("4");
            Optional<AccessGroup> optionalAccessGroup = accessGroupService.findById(e.getAccessGroupId());
            if (optionalAccessGroup.isEmpty()){
                toSave.setAccessGroup(null);
            }
            else{
                toSave.setAccessGroup(optionalAccessGroup.get());
            }

            toSave.setEntranceEventType(entranceEventTypeRepository.getById(e.getEventActionTypeId()));

            toSave.setAuthMethod(authMethodRepository.getById(e.getAuthMethodId()));

            System.out.println("5");
            entranceEventRepository.save(toSave);

        }}
        catch (Exception e){
            System.out.println(e);
            success = false;
        }
        System.out.println("6");
        return success;
    }

    public List<EntranceEvent> getAllEvents(){
        List<EntranceEvent> allEvents = entranceEventRepository.findByDeletedIsFalseOrderByEventTimeDesc();
        return allEvents;

    }
//
//    public MappingJacksonValue filterAllEvents(){
//        SimpleBeanPropertyFilter simpleBeanPropertyFilter =
//                SimpleBeanPropertyFilter.serializeAllExcept("id", "dob");
//
//        FilterProvider filterProvider = new SimpleFilterProvider()
//                .addFilter("userFilter", simpleBeanPropertyFilter);
//
//        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue();
//        mappingJacksonValue.setFilters(filterProvider);
//
//        return mappingJacksonValue;
//    }
//
//    public MappingJacksonValue filterAllEvents(List<Event> AllEvents){
//        SimpleBeanPropertyFilter simpleBeanPropertyFilter =
//                SimpleBeanPropertyFilter.serializeAllExcept("id", "dob");
//
//        FilterProvider filterProvider = new SimpleFilterProvider()
//                .addFilter("eventFilter", simpleBeanPropertyFilter);
//
//        List<User> userList = userService.getAllUsers();
//        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(userList);
//        mappingJacksonValue.setFilters(filterProvider);
//
//        return mappingJacksonValue;
//    }

}
