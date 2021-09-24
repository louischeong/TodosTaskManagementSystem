package com.example.todostaskmanagementsystem.model;

public class Member {
    private String email;
    private String name;
    private String role[];

    public Member(String email, String name, String[] role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public Member(String email) {
        this.email = email;
        this.name = null;
        this.role = null;
    }
    public Member() {
        this.email = "";
        this.name = "";
        this.role = null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getRole() {
        return role;
    }

    public void setRole(String[] role) {
        this.role = role;
    }
}
