package com.aurawin.core.time;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class Time {
    public static final long Second = 1000;  // milliseconds
    public static final long Minute = 60*Second;  // milliseconds

    public static Date incMinutes(Date Current, int Duration){
        long msCurrent = Current.getTime();
        return new Date(msCurrent+(Duration*Minute));
    }
    public static Date incSeconds(Date Current, int Duration){
        long msCurrent = Current.getTime();
        return new Date(msCurrent+(Duration*Second));
    }
    public static Date incMilliSeconds(Date Current, int Duration){
        long msCurrent = Current.getTime();
        return new Date(msCurrent+Duration);
    }
    public static long dtUTC(){
        return ZonedDateTime.now(ZoneOffset.UTC).toInstant().getEpochSecond();
    }
}
