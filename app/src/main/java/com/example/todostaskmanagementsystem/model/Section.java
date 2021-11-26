package com.example.todostaskmanagementsystem.model;


import java.util.List;

public class Section {
    private String id;
    private String name;
    private List<String> allowedEdit;
    private List<String> allowedMark;

    public Section() {

    }

    public Section(String id, String name, List<String> allowedEdit, List<String> allowedMark) {
        this.id = id;
        this.name = name;
        this.allowedEdit = allowedEdit;
        this.allowedMark = allowedMark;
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

    @Override
    public String toString() {
        return "Section{" +
                "name='" + name + '\'' +
                '}';
    }

    public List<String> getAllowedEdit() {
        return allowedEdit;
    }

    public void setAllowedEdit(List<String> allowedEdit) {
        this.allowedEdit = allowedEdit;
    }

    public List<String> getAllowedMark() {
        return allowedMark;
    }

    public void setAllowedMark(List<String> allowedMark) {
        this.allowedMark = allowedMark;
    }
}
