package com.hungerbox.customer.model;

/**
 * Created by manas on 14/3/17.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrderStatusFilter {
    @SerializedName("status")
    public ArrayList<String> status;
}