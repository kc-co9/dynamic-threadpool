package com.share.co.kcl.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    private DateUtils() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static LocalDateTime after(long time, ChronoUnit timeUnit) {
        return now().plus(time, timeUnit);
    }

    public static long valueOfSecond(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public static long valueOfMilliSecond(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
