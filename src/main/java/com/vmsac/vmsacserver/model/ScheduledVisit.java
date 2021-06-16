package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="ScheduledVisit")
public class ScheduledVisit {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="scheduledvisitid")
    private Long scheduledVisitId;
    @Column(name="visitorId")
    private String visitorId;
    @Column(name="startdateofvisit")
    private String startDateofVisit;
    @Column(name="enddateofvisit")
    private String endDateofVisit;
    @Column(name="qrCodeId")
    private String qrCodeId;
    @Column(name="isvalid")
    private boolean isValid;
    @Column(name="isonetimeuse")
    private boolean isOneTimeUse;
    @Column(name="raisedBy")
    private String raisedBy;
}
