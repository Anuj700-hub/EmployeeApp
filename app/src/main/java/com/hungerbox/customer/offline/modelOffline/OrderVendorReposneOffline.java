package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderVendorReposneOffline  implements Serializable {
    static final long serialVersionUID = 1L;

    @SerializedName("data")
    public OrderVendorOffline vendor;

    public OrderVendorOffline getVendor() {
        if (vendor == null) {
            vendor = new OrderVendorOffline();
        }

        return vendor;
    }
}