package com.example.todostaskmanagementsystem.model;

public class Role {
    private String roleName, desc, id;

    public Role(String roleName, String desc, String id) {
        this.roleName = roleName;
        this.desc = desc;
        this.id = id;
    }

    public Role(){
        this.roleName = "";
        this.desc = "";
        this.id = "";
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
