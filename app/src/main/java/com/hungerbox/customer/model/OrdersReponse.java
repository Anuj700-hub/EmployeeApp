package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by peeyush on 7/7/16.
 */
public class OrdersReponse {


    @SerializedName("data")
    public ArrayList<Order> orders;
}
