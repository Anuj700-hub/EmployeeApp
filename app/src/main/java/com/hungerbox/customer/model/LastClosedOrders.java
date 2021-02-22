package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LastClosedOrders {

    @SerializedName("data")
    ArrayList<Order> lastClosedOrdersList;

    public ArrayList<Order> getLastClosedOrdersList() {
        if(lastClosedOrdersList == null){
            return new ArrayList<Order>();
        }
        return lastClosedOrdersList;
    }

    public void setLastClosedOrdersList(ArrayList<Order> lastClosedOrdersList) {
        this.lastClosedOrdersList = lastClosedOrdersList;
    }
}
