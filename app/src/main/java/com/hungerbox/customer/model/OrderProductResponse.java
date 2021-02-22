package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by peeyush on 5/8/16.
 */
public class OrderProductResponse implements Serializable {
    static final long serialVersionUID = 1L;

    @SerializedName("data")
    public ArrayList<OrderProduct> products;
}
