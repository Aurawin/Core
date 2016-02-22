package com.aurawin.core.time;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

public class Time {
    public static final long Second = 1000;  // milliseconds
    public static final long Minute = 60*Second;  // milliseconds
    public static final SimpleDateFormat fmt822 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z",Locale.US);

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

    public static String rfc822(Date Value){
        return fmt822.format(Value);
    }
}
