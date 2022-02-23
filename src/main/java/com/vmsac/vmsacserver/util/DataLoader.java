//package com.vmsac.vmsacserver.util;
//
//import com.vmsac.vmsacserver.model.AccessGroup;
//import com.vmsac.vmsacserver.model.Person;
//import com.vmsac.vmsacserver.repository.AccessGroupRepository;
//import com.vmsac.vmsacserver.repository.PersonRepository;
//import com.vmsac.vmsacserver.repository.PlainPersonRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//@Profile("dev")
//@Component
//public class DataLoader implements CommandLineRunner {
//    private final PersonRepository personRepository;
//    private final AccessGroupRepository accessGroupRepository;
//    private final PlainPersonRepository plainPersonRepository;
//
//    public DataLoader(PersonRepository personRepository, AccessGroupRepository accessGroupRepository, PlainPersonRepository plainPersonRepository) {
//        this.personRepository = personRepository;
//        this.accessGroupRepository = accessGroupRepository;
//        this.plainPersonRepository = plainPersonRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // create person1 and accessgroup1
//        Person person1 = new Person();
//        person1.setPersonFirstName("firstName1");
//        person1.setPersonLastName("lastName1");
//        person1.setDeleted(false);
//        person1.setPersonUid("1");
//        personRepository.save(person1);
//
//        Person person2 = new Person();
//        person2.setPersonFirstName("firstName2");
//        person2.setPersonLastName("lastName2");
//        person2.setDeleted(false);
//        person2.setPersonUid("2");
//        personRepository.save(person2);
//
//        AccessGroup accessGroup = new AccessGroup();
//        accessGroup.setAccessGroupName("accessGroupName");
//        accessGroup.setDeleted(false);
//        accessGroupRepository.save(accessGroup);
//
//        // simulate updating of person access group with wrong access group info
//
//        // person with wrong access group info
//        Person personToUpdate1 = new Person();
//        personToUpdate1.setPersonId(1L);
//        personToUpdate1.setPersonFirstName("newFirstName1");
//        personToUpdate1.setPersonLastName("newLastName1");
//        personToUpdate1.setDeleted(false);
//        personToUpdate1.setPersonUid("1");
//
//        AccessGroup wrongAccessGroup = new AccessGroup();
//        wrongAccessGroup.setAccessGroupId(1L);
//        wrongAccessGroup.setAccessGroupName("wrongAccessGroupName");
//        personToUpdate1.setAccessGroup(wrongAccessGroup);
//
//        // controller should update person with similar logic to below
//        AccessGroup accessGroupToAssign = accessGroupRepository.findById(wrongAccessGroup.getAccessGroupId()).get();
//        personToUpdate1.setAccessGroup(accessGroupToAssign);
//        personRepository.save(personToUpdate1);
//
//        // notice access group name is not changed but person with id 1 info is changed
//
//        // person with wrong access group info
//        Person personToUpdate2 = new Person();
//        personToUpdate2.setPersonId(2L);
//        personToUpdate2.setPersonFirstName("newFirstName2");
//        personToUpdate2.setPersonLastName("newLastName2");
//        personToUpdate2.setDeleted(false);
//        personToUpdate2.setPersonUid("2");
//
//        personToUpdate2.setAccessGroup(wrongAccessGroup);
//
//        // controller actions
//        PlainPerson plainPerson = new PlainPerson();
//        plainPerson.setPersonId(personToUpdate2.getPersonId()); // DO NOT DO THIS, create a method to convert Person to PlainPerson and vice versa
//        plainPerson.setPersonFirstName(personToUpdate2.getPersonFirstName());
//        plainPerson.setPersonLastName(personToUpdate2.getPersonLastName());
//        plainPerson.setDeleted(personToUpdate2.getDeleted());
//        plainPerson.setPersonUid(personToUpdate2.getPersonUid());
//        plainPerson.setAccessGroupId(personToUpdate2.getAccessGroup().getAccessGroupId());
//
//        plainPersonRepository.save(plainPerson);
//
//        // notice access group name is not changed but person with id 2 info is changed
//    }
//}
