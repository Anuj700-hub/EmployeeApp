package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderOptionItemOffline implements Serializable {
    static final long serialVersionUID = 1L;

    long id;
    @SerializedName("vendor_menu_option_id")
    long menuOptionId;
    @SerializedName("name")
    String name;
    @SerializedName("description")
    String desc;
    @SerializedName("display_number")
    int displayNumber;
    @SerializedName("price")
    double price;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMenuOptionId() {
        return menuOptionId;
    }

    public void setMenuOptionId(long menuOptionId) {
        this.menuOptionId = menuOptionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getDisplayNumber() {
        return displayNumber;
    }

    public void setDisplayNumber(int displayNumber) {
        this.displayNumber = displayNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public OrderOptionItemOffline copy(OptionItemOffline optionItem) {
        id = optionItem.getId();
        menuOptionId = optionItem.getMenuOptionId();
        name = optionItem.getName();
        desc = optionItem.getDesc();
        price = optionItem.getPrice();
        return this;
    }
}
