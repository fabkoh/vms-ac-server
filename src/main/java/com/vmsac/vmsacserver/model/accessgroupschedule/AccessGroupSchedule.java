package com.vmsac.vmsacserver.model.accessgroupschedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "accessgroupschedule")
@Builder
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

    @Column(name = "grouptoentranceid")
    private Long groupToEntranceId;

    @Column(name = "isactive")
    private Boolean isActive;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "grouptoentranceid", insertable = false, updatable = false)
//    private AccessGroupEntranceNtoN accessGroupEntranceNtoN; // left here in case require in the future

    @Column(name = "deleted")
    private Boolean deleted;

    public AccessGroupScheduleDto toDto(){
        return new AccessGroupScheduleDto(this.accessGroupScheduleId,this.accessGroupScheduleName,this.rrule,this.timeStart,this.timeEnd, this.groupToEntranceId, this.isActive);
    }
}
