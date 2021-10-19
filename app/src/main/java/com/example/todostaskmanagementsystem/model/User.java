package com.example.todostaskmanagementsystem.model;

public class User {
    private String password;

    public User(String password) {
        this.password = password;
    }

    public User() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
