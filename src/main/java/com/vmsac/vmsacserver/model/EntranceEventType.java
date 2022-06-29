package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="entranceeventtype")
@Builder
public class EntranceEventType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actiontypeid", columnDefinition = "serial")
    private Long actionTypeId;

    @Column(name="actiontypename")
    private String actionTypeName;

    @Column(name="endtypeconfig")
    private String endTypeConfig;
}
