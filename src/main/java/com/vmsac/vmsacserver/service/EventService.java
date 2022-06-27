package com.vmsac.vmsacserver.service;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.vmsac.vmsacserver.model.*;
import com.vmsac.vmsacserver.repository.EventActionTypeRepository;
import com.vmsac.vmsacserver.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private EntranceService entranceService;

    @Autowired
    private AccessGroupService accessGroupService;

    @Autowired
    private ControllerService controllerService;

    @Autowired
    private EventActionTypeRepository eventActionTypeRepository;

    public Boolean createEvents(List<Event> newEvents){
        // iterate over the whole list
        // check if it exists in db
        // check if linked columns are valid
        // record and save
        Boolean success = true;
        System.out.println(newEvents);

        try{
        for (Event singleEvent : newEvents){

            if (eventRepository.existsByEventTimeEqualsAndController_ControllerSerialNoEqualsAndEventActionType_EventActionTypeId(singleEvent.getEventTime(), singleEvent.getController().getControllerSerialNo(),singleEvent.getEventActionType().getEventActionTypeId())){
                continue;
            }

            Event toSave = new Event();
            System.out.println("1");
            toSave.setDirection(singleEvent.getDirection());
            toSave.setEventTime(singleEvent.getEventTime());
            toSave.setDeleted(false);

            System.out.println(singleEvent.getPerson());
            if (singleEvent.getPerson() != null){
            Optional<Person> optionalPerson = personService.findByIdInUse(singleEvent.getPerson().getPersonId());
            if (optionalPerson.isEmpty()){
                toSave.setPerson(null);
            }
            else{
                toSave.setPerson(optionalPerson.get());
            }}
            else{
                toSave.setPerson(null);
            }

            System.out.println("3");
            if (singleEvent.getEntrance() != null){
            Optional<Entrance> optionalEntrance = entranceService.findById(singleEvent.getEntrance().getEntranceId());
            if (optionalEntrance.isEmpty()){
                toSave.setEntrance(null);
            }
            else{
                toSave.setEntrance(optionalEntrance.get());
            }}
            else{
                toSave.setEntrance(null);
            }
            System.out.println("4");
            if (singleEvent.getAccessGroup() != null){
            Optional<AccessGroup> optionalAccessGroup = accessGroupService.findById(singleEvent.getAccessGroup().getAccessGroupId());
            if (optionalAccessGroup.isEmpty()){
                toSave.setAccessGroup(null);
            }
            else{
                toSave.setAccessGroup(optionalAccessGroup.get());
            }}
            else{
                toSave.setAccessGroup(null);
            }

            toSave.setEventActionType(eventActionTypeRepository.getById(singleEvent.getEventActionType().getEventActionTypeId()));

            Optional<Controller> optionalController = controllerService.findBySerialNo(singleEvent.getController().getControllerSerialNo());
            if (optionalController.isEmpty()){
                toSave.setController(null);
            }
            else{
                toSave.setController(optionalController.get());
            }
            System.out.println("5");
            eventRepository.save(toSave);

        }}
        catch (Exception e){
            System.out.println(e);
            success = false;
        }
        System.out.println("6");
        return success;
    }

    public List<Event> getAllEvents(){
        List<Event> allEvents = eventRepository.findByDeletedIsFalseOrderByEventTimeDesc();
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
