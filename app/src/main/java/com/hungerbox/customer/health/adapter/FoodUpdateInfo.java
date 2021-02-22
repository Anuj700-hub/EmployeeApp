package com.hungerbox.customer.health.adapter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FoodUpdateInfo implements Serializable {
    @SerializedName("calorie_intake_id")
    @Expose
    private long id;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
