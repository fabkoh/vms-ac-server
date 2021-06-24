package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="scheduledvisit")
public class ScheduledVisit {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="scheduledvisitid")
    private Long scheduledVisitId;

    @Column(name="visitorid")
    private Long visitorId;

    @Column(name="startdateofvisit")
    private Date startDateOfVisit;

    @Column(name="enddateofvisit")
    private Date endDateOfVisit;

    @Column(name="qrcodeid")
    private String qrCodeId;

    @Column(name="valid")
    private boolean valid;

    @Column(name="onetimeuse")
    private boolean oneTimeUse;

    @Column(name="raisedby")
    private Long raisedBy;

    @ManyToOne()
    @JoinColumn(name="visitorid", insertable=false, updatable=false)
    @JsonBackReference
    private Visitor visitor;
}
