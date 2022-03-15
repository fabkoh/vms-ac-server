package com.vmsac.vmsacserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "accessgroupschedule")
public class AccessGroupSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accessgroupscheduleid", columnDefinition = "serial")
    private Long accessGroupScheduleId;

    @Column(name = "accessgroupschedulename")
    private String accessGroupScheduleName;

    @Column(name = "rrule")
    private String rrule;

    @Column(name = "timestart")
    private String timeStart;

    @Column(name = "timeend")
    private String timeEnd;

    //link to accgrpentrance NtoN

    @Column(name = "deleted")
    private Boolean deleted;

    public AccessGroupScheduleDto toDto(){
        return new AccessGroupScheduleDto(this.accessGroupScheduleId,this.accessGroupScheduleName,this.rrule,this.timeStart,this.timeEnd);
    }
}
