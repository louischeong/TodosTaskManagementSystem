package com.example.todostaskmanagementsystem.model;

public class ChangesLog {
    private String dateTime;
    private String username;
    private String changesType;

    public ChangesLog(){

    }

    public ChangesLog(String dateTime, String username, String changesType) {
        this.dateTime = dateTime;
        this.username = username;
        this.changesType = changesType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChangesType() {
        return changesType;
    }

    public void setChangesType(String changesType) {
        this.changesType = changesType;
    }
}
