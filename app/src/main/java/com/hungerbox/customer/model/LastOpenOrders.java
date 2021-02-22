package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LastOpenOrders {

    @SerializedName("data")
    ArrayList<Order> lastOpenOrdersList;

    public ArrayList<Order> getLastOpenOrdersList() {
        if(lastOpenOrdersList == null){
            return new ArrayList<Order>();
        }
        return lastOpenOrdersList;
    }

    public void setLastOpenOrdersList(ArrayList<Order> lastOpenOrdersList) {
        this.lastOpenOrdersList = lastOpenOrdersList;
    }
}
