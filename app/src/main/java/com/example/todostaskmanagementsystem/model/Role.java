package com.example.todostaskmanagementsystem.model;

public class Role {
    private String roleName, desc;

    public Role(String roleName, String desc) {
        this.roleName = roleName;
        this.desc = desc;
    }

    public Role(){
        this.roleName = " ";
        this.desc = " ";
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
}
