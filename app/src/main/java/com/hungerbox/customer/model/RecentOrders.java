package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;


public class RecentOrders {

    @SerializedName("lastClosedOrders")
    LastClosedOrders lastClosedOrders;

    @SerializedName("lastOpenOrders")
    LastOpenOrders lastOpenOrders;

    public LastClosedOrders getLastClosedOrders() {
        if(lastClosedOrders == null){
            return new LastClosedOrders();
        }
        return lastClosedOrders;
    }

    public void setLastClosedOrders(LastClosedOrders lastClosedOrders) {
        this.lastClosedOrders = lastClosedOrders;
    }

    public LastOpenOrders getLastOpenOrders() {
        if(lastOpenOrders == null){
            return new LastOpenOrders();
        }
        return lastOpenOrders;
    }

    public void setLastOpenOrders(LastOpenOrders lastOpenOrders) {
        this.lastOpenOrders = lastOpenOrders;
    }
}
