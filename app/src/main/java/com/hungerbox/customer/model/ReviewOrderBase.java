package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 1/23/18.
 */

public class ReviewOrderBase {

    @SerializedName("order_id")
    long orderId;
    @SerializedName("vendor_order_ref")
    String vendorOrderRef;
    @SerializedName("vendor")
    String vendor;
    @SerializedName("ReviewOrderItem")
    ArrayList<ReviewOrderItem> reviewOrderItems;


}
