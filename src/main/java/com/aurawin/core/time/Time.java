package com.aurawin.core.time;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Time {
    public static final long Second = 1000;  // milliseconds
    public static final long Minute = 60*Second;  // milliseconds
    public static final SimpleDateFormat fmt822 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z",Locale.US);

    public static final SimpleDateFormat fmtDateOnly = new SimpleDateFormat("MMMM dd yyyy",Locale.US);
    public static final SimpleDateFormat fmtTimeOnly = new SimpleDateFormat("HH:mm:ss",Locale.US);
    public static final SimpleDateFormat fmtYearOnly = new SimpleDateFormat("yyyy",Locale.US);


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
    public static Instant instantUTC(){
        return ZonedDateTime.now(ZoneOffset.UTC).toInstant();
    }
    public static long dtUTC(){
        return ZonedDateTime.now(ZoneOffset.UTC).toInstant().getEpochSecond();
    }

    public static String rfc822(Date Value){
        return fmt822.format(Value);
    }
    public static String getInternet(Instant Value){
        return Value.toString();

    }
    public static String dateOnly(Date Value){ return fmtDateOnly.format(Value);}
    public static String timeOnly(Date Value){ return fmtTimeOnly.format(Value);}
    public static String yearOnly(Date Value){ return fmtYearOnly.format(Value);}
}
