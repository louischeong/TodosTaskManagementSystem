package com.example.todostaskmanagementsystem.model;

import java.util.ArrayList;
import java.util.List;

public class Role {
    private String id, roleName, desc;
    private List<String> members;

    public Role(){

    }

    public Role(String id, String roleName, String desc) {
        this.id = id;
        this.roleName = roleName;
        this.desc = desc;
    }

    public Role(String id, String roleName, String desc, List<String> members) {
        this.id = id;
        this.roleName = roleName;
        this.desc = desc;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
