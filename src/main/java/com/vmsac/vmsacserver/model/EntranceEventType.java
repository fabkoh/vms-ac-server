package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.json.simple.JSONObject;

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

    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    @Column(name="endtypeconfig", columnDefinition = "json")
    private String endTypeConfig;
}
