package com.mfgpker.worklog.data;

import android.text.TextUtils;

import com.google.firebase.database.Exclude;
import com.mfgpker.worklog.firebase.LogsWrapper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Logs implements Serializable, Comparable<Logs> {
    public  static final long serialVersionUID = 1337L;

    public String companyName;
    public LocalDateTime date;
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public int breakMinutes;
    public WorkType workType;

    public  Logs()  { }

    public  Logs (LogsWrapper log) {
        if (log != null) {
            companyName = log.companyName;
            date = log.date != null && !TextUtils.isEmpty(log.date) ? LocalDateTime.parse(log.date) : null;
            startTime =  log.startTime != null && !TextUtils.isEmpty(log.startTime) ? LocalDateTime.parse(log.startTime) : null;
            endTime = log.endTime != null && !TextUtils.isEmpty(log.endTime) ? LocalDateTime.parse(log.endTime) : null;
            breakMinutes = log.breakMinutes;
            workType = log.workType;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Logs logs = (Logs) o;
        return breakMinutes == logs.breakMinutes && companyName.equals(logs.companyName) && date.equals(logs.date) && Objects.equals(startTime, logs.startTime) && Objects.equals(endTime, logs.endTime) && workType == logs.workType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, date, startTime, endTime, breakMinutes, workType);
    }

    @Override
    public String toString() {
        return "Logs{" +
                "companyName='" + companyName + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", breakMinutes=" + breakMinutes +
                ", workType=" + workType +
                '}';
    }

    @Override
    public int compareTo(Logs log) {
        return log.date.compareTo(date);
    }
}
