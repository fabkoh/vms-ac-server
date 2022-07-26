package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "genconfigs")
public class GENConfigs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "controllerid")
    private Controller controller;

    @Column(name = "pinname")
    private String pinName;

    @Column(name = "status")
    private String status;

    public String getInName() {
        return "GEN_IN_" + getPinName();
    }

    public String getOutName() {
        return "GEN_OUT_" + getPinName();
    }
}
