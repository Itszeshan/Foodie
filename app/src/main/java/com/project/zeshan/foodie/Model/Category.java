package com.project.zeshan.foodie.Model;

public class Category {

    private String name;
    private String image;

    public Category()
    {

    }

    public Category(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
