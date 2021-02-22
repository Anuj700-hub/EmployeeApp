package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 5/7/16.
 */
public class OrderConfirm {

    @SerializedName("delivery_time")
    public long estimatedTime;

    @SerializedName("order_id")
    public long orderId;

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;
    /*
    qr, nfc, pin, null
     */
    @SerializedName("delivery_method")
    public String deliverymethod = "null";
}
