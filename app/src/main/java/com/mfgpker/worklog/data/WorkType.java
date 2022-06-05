package com.mfgpker.worklog.data;

public enum WorkType {
    NORMAL ("Normal"),
    HOLIDAY ("Holiday"),
    SICK ("Sick"),
    WORK_FROM_HOME ( "Home"),
    WORK_FROM_AWAY ( "AWAY"),
    FREE ( "Free"),
    UNEMPLOYED ( "Unemployed");

    private String workType;

    WorkType(String workType) {
        this.workType = workType;
    }

    public WorkType toWorkType(int id) {
        switch (id) {
            case (0):
                return NORMAL;
            case (1):
                return HOLIDAY;
            case (2):
                return SICK;
            case 3:
                return WORK_FROM_HOME;
            case 4:
                return WORK_FROM_AWAY;
            case 5:
                return FREE;
            case 6:
                return UNEMPLOYED;
            default:
                return null;
        }
    }

    public int toInt() {
        switch (workType) {
            case "Normal":
                return 0;
            case "Holiday":
                return 1;
            case "Sick":
                return 2;
            case "Home":
                return 3;
            case "AWAY":
                return 4;
            case "Free":
                return 5;
            case "Unemployed":
                return 6;
            default:
                return -1;
        }
    }

    @Override
    public String toString(){
        return workType;
    }
}