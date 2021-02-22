package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class RecentOrdersResponse {
    @SerializedName("data")
    RecentOrders recentOrders;

    public RecentOrders getRecentOrders() {
        if (recentOrders == null) {
            recentOrders = new RecentOrders();
        }
        return recentOrders;
    }

    public void setRecentOrders(RecentOrders recentOrders) {
        this.recentOrders = recentOrders;
    }
}
