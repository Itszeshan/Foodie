package com.project.zeshan.foodie.Model;

public class User {

    private String email;
    private String name;
    private String fname;
    private String phone;

    public User(String email, String name, String fname, String phone) {
        this.email = email;
        this.name = name;
        this.fname = fname;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getFname() {
        return fname;
    }
}
