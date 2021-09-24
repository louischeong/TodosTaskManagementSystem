package com.example.todostaskmanagementsystem.model;

public class Todolist {
    private String name, desc;
    private int incomplete;

    public Todolist(String name, String desc) {
        this.name = name;
        this.desc = desc;
        this.incomplete = 0;
    }

    public Todolist(String name, String desc, int incomplete) {
        this.name = name;
        this.desc = desc;
        this.incomplete = incomplete;
    }
    public Todolist() {
        this.name = null;
        this.desc = null;
        this.incomplete = 0;
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

    public int getIncomplete() {
        return incomplete;
    }

    public void setIncomplete(int incomplete) {
        this.incomplete = incomplete;
    }
}
