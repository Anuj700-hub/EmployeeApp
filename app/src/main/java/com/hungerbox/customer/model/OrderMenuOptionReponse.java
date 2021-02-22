package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sandipanmitra on 10/27/16.
 */
public class OrderMenuOptionReponse implements Serializable {
    static final long serialVersionUID = 1L;

    @SerializedName("data")
    public ArrayList<OrderSubProduct> subProducts;

    public ArrayList<OrderSubProduct> getSubProducts() {
        if (subProducts == null)
            subProducts = new ArrayList<>();
        return subProducts;
    }

    public void setSubProducts(ArrayList<OrderSubProduct> subProducts) {
        this.subProducts = subProducts;
    }
}
