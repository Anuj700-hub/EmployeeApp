package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderProductResponseOffline implements Serializable {
    static final long serialVersionUID = 1L;

    @SerializedName("data")
    public ArrayList<OrderProductOffline> products;
}

