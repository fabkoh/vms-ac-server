package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "visitor")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visitorid")
    private Long visitorId;

    @Column(name = "visitoruid", unique = true, nullable = false)
    @NotBlank(message = "Visitor UID is required")
    @JsonAlias("idNumber")
    private String visitorUid;

    @NotBlank(message = "First name is required")
    @Column(name = "firstname")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "lastname")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Column(name = "emailadd")
    private String emailAdd;

    @Column(name = "mobilenumber")
    private String mobileNumber;

    @Column(name = "company")
    private String company;

    @OneToMany(mappedBy = "visitor", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ScheduledVisit> scheduledVisits;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "personid", nullable = true)
    @JsonIgnore
    private Person person;
}
