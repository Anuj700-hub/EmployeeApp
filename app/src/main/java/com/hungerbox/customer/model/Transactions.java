package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Transactions implements Serializable {

    @SerializedName("data")

    ArrayList<PostPaidOrder> postPaidOrders;

    public ArrayList<PostPaidOrder> getPostPaidOrders() {
        return postPaidOrders;
    }

    public void setPostPaidOrders(ArrayList<PostPaidOrder> postPaidOrders) {
        this.postPaidOrders = postPaidOrders;
    }
}
