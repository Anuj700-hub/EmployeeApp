package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sandipanmitra on 9/22/17.
 */

public class OrderPaymentResponse implements Serializable {
    @SerializedName("data")
    OrderPaymentData orderPaymentData;

    public OrderPaymentData getOrderPaymentData() {
        return orderPaymentData;
    }

    public void setOrderPaymentData(OrderPaymentData orderPaymentData) {
        this.orderPaymentData = orderPaymentData;
    }
}
