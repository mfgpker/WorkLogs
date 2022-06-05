package com.mfgpker.worklog.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

    public static TimeDifference getTimeDifference(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime tempDateTime = LocalDateTime.from( startTime );

        long years = tempDateTime.until( endTime, ChronoUnit.YEARS );
        tempDateTime = tempDateTime.plusYears( years );

        long months = tempDateTime.until( endTime, ChronoUnit.MONTHS );
        tempDateTime = tempDateTime.plusMonths( months );

        long days = tempDateTime.until( endTime, ChronoUnit.DAYS );
        tempDateTime = tempDateTime.plusDays( days );


        long hours = tempDateTime.until( endTime, ChronoUnit.HOURS );
        tempDateTime = tempDateTime.plusHours( hours );

        long minutes = tempDateTime.until( endTime, ChronoUnit.MINUTES );
        tempDateTime = tempDateTime.plusMinutes( minutes );

        long seconds = tempDateTime.until( endTime, ChronoUnit.SECONDS );

        return new TimeDifference(years, months, days, hours, minutes, seconds);
    }

    public static  long getTimeStamp(LocalDateTime date) {
        Instant instant = date.toInstant(ZoneOffset.UTC);

        return instant.getEpochSecond();
    }

    public static  LocalDateTime getLocalDateTime(Long timeStamp) {
        Instant instant = Instant.ofEpochSecond(timeStamp);

        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }

    public static  LocalDateTime getStartOfDay(LocalDateTime date) {
        return LocalDateTime.of(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth(), 0,0,0);
    }

    public static  LocalDateTime getEndOfDay(LocalDateTime date) {
        return LocalDateTime.of(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth(), 23,59,59);
    }

    public static  long getStartOfDayEpoch(LocalDateTime date) {
        return getTimeStamp(getStartOfDay(date));
    }

    public static  long getEndOfDayEpoch(LocalDateTime date) {
        return getTimeStamp(getEndOfDay(date));
    }

    public static  LocalDateTime getStartOfMonth(LocalDateTime date) {
        return LocalDateTime.of(date.getYear(), date.getMonth().getValue(), 1, 0,0,0);
    }

    public static  LocalDateTime getEndOfMonth(LocalDateTime date) {
        LocalDate localDate = date.toLocalDate();
        return LocalDateTime.of(localDate.getYear(), localDate.getMonth().getValue(), localDate.lengthOfMonth(), 23,59,59);
    }

    public static  long getStartOfMonthEpoch(LocalDateTime date) {
        return getTimeStamp(getStartOfMonth(date));
    }

    public static  long getEndOfMonthEpoch(LocalDateTime date) {
        return getTimeStamp(getEndOfMonth(date));
    }

    public static  LocalDateTime getStartOfYear(LocalDateTime date) {
        return LocalDateTime.of(date.getYear(), 1, 1, 0,0,0);
    }

    public static  LocalDateTime getEndOfYear(LocalDateTime date) {
        LocalDate localDate = date.toLocalDate();
        return LocalDateTime.of(localDate.getYear(), 12, 31, 23,59,59);
    }

    public static  long getStartOfYearEpoch(LocalDateTime date) {
        return getTimeStamp(getStartOfYear(date));
    }

    public static  long getEndOfYearEpoch(LocalDateTime date) {
        return getTimeStamp(getEndOfYear(date));
    }

    public static LocalDateTime getDate(int year) {

        return LocalDateTime.of(year, 1 , 1, 0,0,0);
    }

    public static LocalDateTime getDate(int year, int month) {

        if (month >= 1 && month <= 12) {
            return LocalDateTime.of(year, month , 1, 0,0,0);
        } else {
            return  null;
        }
    }
    public static LocalDateTime getDate(int year, int month, int day) {

        return LocalDateTime.of(year, month , day, 0,0,0);
    }

    public boolean sameDay(LocalDateTime date1, LocalDateTime date2) {
        return date1.getYear() == date2.getYear() &&
                date1.getMonthValue() == date2.getMonthValue() &&
                date1.getDayOfMonth() == date2.getDayOfMonth();
    }

    public static class TimeDifference {
        public long years = 0;
        public long months = 0;
        public long days = 0;
        public long hours = 0;
        public long minutes = 0;
        public long seconds = 0;

        public TimeDifference(long years, long months, long days, long hours, long minutes, long seconds) {
            this.years = years;
            this.months = months;
            this.days = days;
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }
    }
}


