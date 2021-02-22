package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 3/15/17.
 */

public class OrderComment {

    @SerializedName("order_id")
    public long orderId;

    @SerializedName("status")
    public String status;

    @SerializedName("comment")
    public String comment;
}

