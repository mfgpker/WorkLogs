package com.mfgpker.worklog.stats;

public class Stats {
    public int normal = 0;
    public int holiday = 0;
    public int sick = 0;
    public int workFromHome = 0;
    public int workFromAway = 0;
    public int free = 0;
    public int unemployed = 0;


    public int addNormal () {
        return ++normal;
    }

    public int addHoliday () {
        return ++holiday;
    }

    public int addSick () {
        return ++sick;
    }

    public int addWorkFromHome () {
        return ++workFromHome;
    }

    public int addWorkFromAway () {
        return ++workFromAway;
    }

    public int addFree () {
        return ++free;
    }

    public int addUnemployed () {
        return ++unemployed;
    }
}
