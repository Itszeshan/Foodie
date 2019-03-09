package com.project.zeshan.foodie.Model;

public class Food {

    private String name;
    private String image;
    private String price;
    private String desc;
    private String discount;
    private String menuId;

    public Food()
    {

    }

    public Food(String name, String image, String price, String desc, String discount, String menuId) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.desc = desc;
        this.discount = discount;
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getDesc() {
        return desc;
    }

    public String getDiscount() {
        return discount;
    }

    public String getMenuId() {
        return menuId;
    }
}
