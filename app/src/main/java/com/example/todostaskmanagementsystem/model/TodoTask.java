package com.example.todostaskmanagementsystem.model;


public class TodoTask {
    private String id;
    private String name;
    private String desc;
    private String dueDate;
    private Boolean complete;
    private int reminder;

    public TodoTask() {

    }

    public TodoTask(String id, String name, String desc, String dueDate) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.dueDate = dueDate;
        this.complete = false;
    }

    public TodoTask(String id, String name, String desc, String dueDate, int reminder) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.dueDate = dueDate;
        this.complete = false;
        this.reminder = reminder;
    }

    public TodoTask(String id, String name, String desc, String dueDate, boolean complete, int reminder) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.dueDate = dueDate;
        this.complete = complete;
        this.reminder = reminder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public int getReminder() {
        return reminder;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }


    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                '}';
    }
}
