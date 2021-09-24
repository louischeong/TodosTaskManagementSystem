package com.example.todostaskmanagementsystem.model;

import java.util.ArrayList;

public class Section {
    private int id;
    private String name;
    private ArrayList<Task> tasks;

    public Section(int id, String name, ArrayList<Task> tasks) {
        this.id = id;
        this.name = name;
        this.tasks = tasks;
    }
    public Section() {
        this.id = 1;
        this.name = "New Section";
        this.tasks = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
