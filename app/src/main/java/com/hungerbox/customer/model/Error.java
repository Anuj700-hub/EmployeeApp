package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by peeyush on 1/10/16.
 */
public class Error {

    @SerializedName("old_order_id")
    public ArrayList<Long> orderIds;

    public ArrayList<Long> getOrderIds() {
        if (orderIds == null) {
            orderIds = new ArrayList<>();
        }
        return orderIds;
    }

    public void setOrderIds(ArrayList<Long> orderIds) {
        this.orderIds = orderIds;
    }
}
