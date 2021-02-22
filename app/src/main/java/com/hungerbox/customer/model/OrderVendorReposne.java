package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by peeyush on 4/8/16.
 */
public class OrderVendorReposne implements Serializable {
    static final long serialVersionUID = 1L;

    @SerializedName("data")
    public OrderVendor vendor;

    public OrderVendor getVendor() {
        if (vendor == null) {
            vendor = new OrderVendor();
        }

        return vendor;
    }
}
