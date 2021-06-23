package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private String startDateOfVisit;

    @Column(name="enddateofvisit")
    private String endDateOfVisit;

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
    private Visitor visitor;
}
