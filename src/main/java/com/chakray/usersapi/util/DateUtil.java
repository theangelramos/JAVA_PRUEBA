package com.chakray.usersapi.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class DateUtil {

    public static String getCurrentMadagascarTime() {
        ZonedDateTime madagascarTime = ZonedDateTime.now(ZoneId.of("Indian/Antananarivo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return madagascarTime.format(formatter);
    }
}
