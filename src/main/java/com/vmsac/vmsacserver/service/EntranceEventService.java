package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.model.authmethod.AuthMethod;
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

            toSave.setDirection(e.getDirection());

            toSave.setEventTime(eventTime);

            toSave.setDeleted(false);

            Optional<Person> optionalPerson = personService.findByIdInUse(e.getPersonId());
            if (optionalPerson.isEmpty()){
                toSave.setPerson(null);
            }
            else{
                toSave.setPerson(optionalPerson.get());
            }

            Optional<Entrance> optionalEntrance = entranceService.findById(e.getEntranceId());
            if (optionalEntrance.isEmpty()){
                // omit entrance events that does not come with a valid entranceId
                continue;
            }
            else{
                toSave.setEntrance(optionalEntrance.get());
            }

            Optional<AccessGroup> optionalAccessGroup = accessGroupService.findById(e.getAccessGroupId());
            if (optionalAccessGroup.isEmpty()){
                toSave.setAccessGroup(null);
            }
            else{
                toSave.setAccessGroup(optionalAccessGroup.get());
            }

            Optional<EntranceEventType> type = entranceEventTypeRepository.findById(e.getEventActionTypeId());
            if (type.isEmpty())
                // omit entrance events that does not come with a valid actionTypeId
                continue;
            else toSave.setEntranceEventType(type.get());

            Optional<AuthMethod> method = authMethodRepository.findById(e.getAuthMethodId());
            if (method.isEmpty())
                toSave.setAuthMethod(null);
            else toSave.setAuthMethod(method.get());

            entranceEventRepository.save(toSave);

        }
        }
        catch (Exception e){
            System.out.println(e);
            success = false;
        }
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
