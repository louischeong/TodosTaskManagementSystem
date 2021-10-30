package com.example.todostaskmanagementsystem.model;


public class Section {
    private String id;
    private String name;

    public Section(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Section() {
        this.id = "S1";
        this.name = "New Section";
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


}
