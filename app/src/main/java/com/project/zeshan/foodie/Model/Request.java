package com.project.zeshan.foodie.Model;

import java.util.List;

public class Request {

    private String id;
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private List<Order> foods;

    public Request(String phone, String name, String address, String total, List<Order> foods) {
        this.phone = phone;
        this.address = address;
        this.total = total;
        this.foods = foods;
        this.name =  name;
        this.status = "0";
        this.id = "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getTotal() {
        return total;
    }

    public List<Order> getFoods() {
        return foods;
    }

}

