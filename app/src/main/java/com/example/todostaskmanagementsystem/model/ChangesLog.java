package com.example.todostaskmanagementsystem.model;

import com.google.firebase.Timestamp;

public class ChangesLog {
    private Timestamp dateTime;
    private String username;
    private String changesType;
    private String parentName;
    private String childName;

    public ChangesLog() {

    }


    public ChangesLog(Timestamp dateTime, String username, String changesType) {
        this.dateTime = dateTime;
        this.username = username;
        this.changesType = changesType;
    }

    public ChangesLog(Timestamp dateTime, String username, String changesType, String parentName) {
        this.dateTime = dateTime;
        this.username = username;
        this.changesType = changesType;
        this.parentName = parentName;
    }

    public ChangesLog(Timestamp dateTime, String username, String changesType, String parentName, String childName) {
        this.dateTime = dateTime;
        this.username = username;
        this.changesType = changesType;
        this.parentName = parentName;
        this.childName = childName;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
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

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }
}
