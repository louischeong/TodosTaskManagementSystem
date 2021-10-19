package com.example.todostaskmanagementsystem.model;

import java.util.ArrayList;

public class Section {
    private String id;
    private String name;
    private ArrayList<TodoTask> todoTasks;

    public Section(String id, String name, ArrayList<TodoTask> todoTasks) {
        this.id = id;
        this.name = name;
        this.todoTasks = todoTasks;
    }

    public Section(String id, String name) {
        this.id = id;
        this.name = name;
        this.todoTasks = null;
    }

    public Section() {
        this.id = "S1";
        this.name = "New Section";
        this.todoTasks = null;
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

    public ArrayList<TodoTask> getTasks() {
        return todoTasks;
    }

    public void setTasks(ArrayList<TodoTask> TodoTasks) {
        this.todoTasks = TodoTasks;
    }

    @Override
    public String toString() {
        return "Section{" +
                "name='" + name + '\'' +
                '}';
    }


}
