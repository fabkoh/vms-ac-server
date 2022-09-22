package com.vmsac.vmsacserver.model.credentialtype.entranceschedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "entranceschedule")
@Builder
public class EntranceSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entrancescheduleid", columnDefinition = "serial")
    private Long entranceScheduleId;

    @Column(name = "entranceschedulename")
    private String entranceScheduleName;

    @Column(name = "rrule")
    private String rrule;

    @Column(name = "timestart")
    private String timeStart;

    @Column(name = "timeend")
    private String timeEnd;

    @Column(name = "entranceid")
    private Long entranceId;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "isactive")
    private Boolean isActive;

    public EntranceScheduleDto toDto(){
        return new EntranceScheduleDto(this.entranceScheduleId,this.entranceScheduleName,this.rrule,this.timeStart,this.timeEnd, this.entranceId, this.isActive);
    }
}
