package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "entrances")
public class Entrance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entranceid", columnDefinition = "serial")
    private Long entranceId;

    @Column(name = "entrancename")
    private String entranceName;

    @Column(name = "entrancedesc")
    private String entranceDesc;

    @Column(name = "isenabled")
    private Boolean isEnabled;

    @Column(name = "deleted")
    private Boolean deleted;

//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "personId")
//    @JsonIgnore
//    @OneToMany(mappedBy = "accessGroup")
//    private List<Person> persons;

    public EntranceDto toDto(){
        return new EntranceDto(this.entranceId, this.entranceName,
                this.entranceDesc,isEnabled);
    }
    public EntranceOnlyDto toEntranceOnlyDto(){
        return new EntranceOnlyDto(this.entranceId,this.entranceName,this.entranceDesc, this.isEnabled);
    }
}
