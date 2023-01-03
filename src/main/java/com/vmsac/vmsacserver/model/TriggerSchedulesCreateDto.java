package com.vmsac.vmsacserver.model;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
public class TriggerSchedulesCreateDto {

    @NotNull
    @NotBlank
    private String triggerName;

    @NotNull
    @NotBlank
    private String rrule;

    @NotNull
    @NotBlank
    private String timeStart;

    @NotNull
    @NotBlank
    private String timeEnd;


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
}
