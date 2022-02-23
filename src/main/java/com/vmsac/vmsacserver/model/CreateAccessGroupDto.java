package com.vmsac.vmsacserver.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class CreateAccessGroupDto {

    @NotBlank(message = "AccessGroupName must not be empty")
    private String accessGroupName;

    private String accessGroupDesc;

//    @OneToMany(mappedBy = "accessGroup", cascade = {CascadeType.ALL, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH})
//    private List<Person> persons = new ArrayList<>();
//
//    public List<Person> getPersons() {
//        return persons;
//    }
//
//    public void setPersons(List<Person> persons) {
//        this.persons = persons;
//    }
    private List<PersonOnlyDto> persons;
    public AccessGroup toAccessGroup(Boolean deleted){
        return new AccessGroup(null,accessGroupName,accessGroupDesc,deleted,null);
    }
}
