package com.vmsac.vmsacserver.util;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class DateTimeParser {

    String[] dateTimeFormats = {"dd-MM-yyyy HH:mm:ss", "MM-dd-yyyy HH:mm:ss",
            "dd-MMM-yyyy HH:mm:ss"};

    public LocalDateTime toLocalDateTime(String datetime) {
        for (String dtf : dateTimeFormats) {
            try {
                return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern(dtf));
            } catch (Exception e) {
                continue;
            }
        }
        throw new DateTimeException("Not supported date time format");
    }
}
