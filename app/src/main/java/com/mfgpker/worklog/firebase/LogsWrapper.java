package com.mfgpker.worklog.firebase;

import com.mfgpker.worklog.data.Logs;
import com.mfgpker.worklog.data.WorkType;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class LogsWrapper implements Serializable {
    public  static final long serialVersionUID = 1337L;

    public String companyName;
    public String date;
    public String startTime;
    public String endTime;
    public int breakMinutes;
    public WorkType workType;

    public  LogsWrapper() {}

    public  LogsWrapper(Logs log) {
        if(log != null) {
            companyName = log.companyName;
            date = log.date != null ? log.date.format(DateTimeFormatter.ISO_DATE_TIME) : "";
            startTime = log.startTime != null ? log.startTime.format(DateTimeFormatter.ISO_DATE_TIME) : "";
            endTime = log.endTime != null ? log.endTime.format(DateTimeFormatter.ISO_DATE_TIME) : "";
            breakMinutes = log.breakMinutes;
            workType = log.workType;
        }
    }
}
