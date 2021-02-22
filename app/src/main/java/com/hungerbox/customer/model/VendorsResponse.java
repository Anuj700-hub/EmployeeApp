package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by peeyush on 4/8/16.
 */
public class VendorsResponse {
    public String error;
    @SerializedName("data")
    public ArrayList<Vendor> vendors;

    public ArrayList<Vendor> getVendors() {
        if (vendors == null)
            vendors = new ArrayList<>();
        return vendors;
    }

    public void setVendors(ArrayList<Vendor> vendors) {
        this.vendors = vendors;
    }
}
