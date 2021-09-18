package com.example.todostaskmanagementsystem.model;

import com.google.firebase.Timestamp;

public class Task {
    private String name;
    private String desc;
    //private Timestamp dueDate;
    private Boolean complete;
    private String reminder;

    public Task(String name, String desc, Boolean complete, String reminder) {
        this.name = name;
        this.desc = desc;
        this.complete = complete;
        this.reminder = reminder;
    }
//    public Task(String name, String desc, Timestamp dueDate, Boolean complete, String reminder) {
//        this.name = name;
//        this.desc = desc;
//        this.dueDate = dueDate;
//        this.complete = complete;
//        this.reminder = reminder;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

//    public Timestamp getDueDate() {
//        return dueDate;
//    }
//
//    public void setDueDate(Timestamp dueDate) {
//        this.dueDate = dueDate;
//    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }
}
