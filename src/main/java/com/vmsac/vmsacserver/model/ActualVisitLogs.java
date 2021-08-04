package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="actualvisitlogs")
public class ActualVisitLogs {

    @Id
    //@GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="logid")
    private String logId;

    @Column(name="timein")
    private String timeIn;

    @Column(name="scanneridin")
    private String scannerIdIn;


  /*  @OneToMany(mappedBy = "visitor", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ScheduledVisit> visitorScheduledVisits;

   */
}
