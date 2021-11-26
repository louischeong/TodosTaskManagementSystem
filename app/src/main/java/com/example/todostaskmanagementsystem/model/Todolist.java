package com.example.todostaskmanagementsystem.model;

import java.util.List;
import java.util.Map;

public class Todolist {
    private String id, name, desc, owner;
    private int incomplete;
    private List<String> membersEmail;

    public Todolist(String id, String name, String desc, String owner, List<String> membersEmail) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.owner = owner;
        this.incomplete = 0;
        this.membersEmail = membersEmail;
    }

    public Todolist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Todolist() {
        this.name = null;
        this.desc = null;
        this.owner = null;
        this.incomplete = 0;
        this.membersEmail = null;
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

    public List<String> getMembersEmail() {
        return membersEmail;
    }

    public void setMembersEmail(List<String> membersEmail) {
        this.membersEmail = membersEmail;
    }

}
