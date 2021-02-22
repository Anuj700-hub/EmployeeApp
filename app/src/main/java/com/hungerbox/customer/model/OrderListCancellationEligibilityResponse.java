package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrderListCancellationEligibilityResponse {

    @SerializedName("data")
    ArrayList<OrderCancellationEligibilityData> data;

    public boolean hasAnyOrderToCancel(){
        if(data.size()== 0)
            return false;
        else
            return true;
    }

    public OrderCancellationEligibilityData getRecentOrder(){
        if(hasAnyOrderToCancel())
            return data.get(0);
        else
            return new OrderCancellationEligibilityData();
    }

    public ArrayList<OrderCancellationEligibilityData> getAllOrders(){
        if(hasAnyOrderToCancel())
            return data;
        else
            return new ArrayList<>();
    }
}
