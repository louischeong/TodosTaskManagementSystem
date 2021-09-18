package com.example.todostaskmanagementsystem.model;

import java.util.ArrayList;

public class Section {
    private String name;
    private ArrayList tasks;

    public Section(String name, ArrayList tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList tasks) {
        this.tasks = tasks;
    }
}
