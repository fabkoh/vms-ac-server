package com.vmsac.vmsacserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="triggerschedules")
@Builder
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
@SQLDelete(sql = "update triggerschedules set deleted=true where triggerscheduleid=?")
@Where(clause = "deleted = false")
public class TriggerSchedules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "triggerscheduleid", columnDefinition = "serial")
    private Long triggerScheduleId;

    @Column(name = "triggername")
    @NotNull
    @NotBlank
    private String triggerName;

    @Column(name = "rrule")
    @NotNull
    @NotBlank
    private String rrule;

    @Column(name = "timestart")
    @NotNull
    private String timeStart;

    @Column(name = "timeend")
    @NotNull
    private String timeEnd;

    @Column(name = "deleted")
    @JsonIgnore
    private Boolean deleted = false;

    // added to store rrule object
    @Column(name = "dstart")
    @NotNull
    private String dtstart;

    @Column(name = "until")
    private String until;

    @Column(name = "count")
    private int count;

    @Column(name = "repeattoggle")
    private Boolean repeatToggle;

    @Column(name = "rruleinterval")
    private int rruleinterval;

    @Type( type = "list-array" )
    @Column(name = "byweekday")
    private ArrayList<Integer> byweekday;

    @Type( type = "list-array" )
    @Column(name = "bymonthday")
    private ArrayList<Integer> bymonthday;

    @Type( type = "list-array" )
    @Column(name = "bysetpos")
    private ArrayList<Integer> bysetpos;

    @Type( type = "list-array" )
    @Column(name = "bymonth")
    private ArrayList<Integer> bymonth;

    @Column(name = "allday")
    private Boolean allDay;

    @Column(name = "endofday")
    private Boolean endOfDay;

    @Override
    public String toString() {
        return "TriggerSchedulesToString";
    }
}
