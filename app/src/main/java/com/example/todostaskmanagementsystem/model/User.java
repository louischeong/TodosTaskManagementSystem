package com.example.todostaskmanagementsystem.model;

public class User {
    private String password;
    private String name;
    private String contact;
    private String email;
    private String token;

    public User(){

    }

    public User(String password, String name, String contact, String email) {
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
