package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "scheduledvisit")
public class ScheduledVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduledvisitid")
    private Long scheduledVisitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitorid")
    @JsonIgnore
    private Visitor visitor;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "visitdate")
    private LocalDate visitDate;

    @Column(name = "qrcodeid")
    private String qrCodeId;

    @Column(name = "valid")
    private boolean valid;

    @Column(name = "onetimeuse")
    private boolean oneTimeUse;

    @Column(name = "raisedby")
    private Long raisedBy;
}
