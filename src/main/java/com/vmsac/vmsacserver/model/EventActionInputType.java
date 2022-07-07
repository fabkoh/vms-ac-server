package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="eventactioninputtype")
@Builder
public class EventActionInputType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventactioninputid", columnDefinition = "serial")
    private Long eventActionInputId;

    @Column(name = "eventactioninputtypename")
    private String eventActionInputTypeName;

    @Column(name = "timerenabled")
    private Boolean timerEnabled;

    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    @Column(name = "eventactioninputtypeconfig")
    private String eventActionInputTypeConfig;

    public EventActionInputTypeDto toDto() {
        return new EventActionInputTypeDto(this.eventActionInputId,
                this.eventActionInputTypeName, this.timerEnabled);
    }
}
