package com.example.todostaskmanagementsystem.model;

public class Reminder {
    private int id;
    private String todolistID;
    private String sectionID;
    private String taskID;
    private int daysRepeat;

    public Reminder() {
    }

    public Reminder(int id, String todolistID, String sectionID, String taskID, int daysRepeat) {
        this.id = id;
        this.todolistID = todolistID;
        this.sectionID = sectionID;
        this.taskID = taskID;
        this.daysRepeat = daysRepeat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTodolistID() {
        return todolistID;
    }

    public void setTodolistID(String todolistID) {
        this.todolistID = todolistID;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public int getDaysRepeat() {
        return daysRepeat;
    }

    public void setDaysRepeat(int daysRepeat) {
        this.daysRepeat = daysRepeat;
    }

}
