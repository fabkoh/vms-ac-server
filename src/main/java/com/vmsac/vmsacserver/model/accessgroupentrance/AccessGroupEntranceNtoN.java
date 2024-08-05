package com.vmsac.vmsacserver.model.accessgroupentrance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.Entrance;
import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.repository.PersonRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "accessgroupsentrancenton")
@Builder
public class AccessGroupEntranceNtoN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grouptoentranceid", columnDefinition = "serial")
    private Long groupToEntranceId;

    @ManyToOne
    @JoinColumn(name="accessgroupid")
    private AccessGroup accessGroup;

    @ManyToOne
    @JoinColumn(name="entranceid")
    private Entrance entrance;

    @Column(name="deleted")
    private Boolean deleted;

    public AccessGroupEntranceNtoNDto toDto() {
        return new AccessGroupEntranceNtoNDto(groupToEntranceId, entrance.toEntranceOnlyDto(), accessGroup.toAccessGroupOnlyDto());
    }

    @JsonIgnore
    public List<Person> getAssignedPersons(PersonRepository repo) {
        List<Person> persons = repo.findAllByAccessGroupAccessGroupIdAndDeletedFalse(this.accessGroup.getAccessGroupId());
        return persons;
    }

}
