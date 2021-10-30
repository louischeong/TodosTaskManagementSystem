package com.example.todostaskmanagementsystem.model;

public class Todolist {
    private String name, desc, owner;
    private int incomplete;

    public Todolist(String name, String desc, String owner) {
        this.name = name;
        this.desc = desc;
        this.owner = owner;
        this.incomplete = 0;
    }

    public Todolist(String name, String desc, String owner, int incomplete) {
        this.name = name;
        this.desc = desc;
        this.owner = owner;
        this.incomplete = incomplete;
    }

    public Todolist() {
        this.name = null;
        this.desc = null;
        this.owner = null;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getIncomplete() {
        return incomplete;
    }

    public void setIncomplete(int incomplete) {
        this.incomplete = incomplete;
    }
}
