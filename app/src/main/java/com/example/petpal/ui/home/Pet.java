package com.example.petpal.ui.home;

public class Pet {
    private int id;
    private String name;
    private String age;
    private String description;

    public Pet(String name, String age, String description) {
        this.name = name;
        this.age = age;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getDescription() {
        return description;
    }
}
