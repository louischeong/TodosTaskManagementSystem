package com.example.todostaskmanagementsystem.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Notification {
    private String todolistID;
    private String todolistTitle;
    private String owner;
    private Timestamp dateTime;
    private List<String> recipientEmails;

    public Notification() {
        this.todolistID = null;
        this.todolistTitle = null;
        this.owner = null;
        this.dateTime = null;
        this.recipientEmails = null;
    }

    public Notification(String todolistID, String todolistTitle, String owner, Timestamp dateTime, List<String> recipientEmails) {
        this.todolistID = todolistID;
        this.todolistTitle = todolistTitle;
        this.owner = owner;
        this.dateTime = dateTime;
        this.recipientEmails = recipientEmails;
    }

    public String getTodolistID() {
        return todolistID;
    }

    public void setTodolistID(String todolistID) {
        this.todolistID = todolistID;
    }

    public String getTodolistTitle() {
        return todolistTitle;
    }

    public void setTodolistTitle(String todolistTitle) {
        this.todolistTitle = todolistTitle;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public List<String> getRecipientEmails() {
        return recipientEmails;
    }

    public void setRecipientEmails(List<String> recipientEmails) {
        this.recipientEmails = recipientEmails;
    }
}
