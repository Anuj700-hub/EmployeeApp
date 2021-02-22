package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AllVendorResponse {

    public static String api_key = "vendor";

    @SerializedName("data")
    private ArrayList<Vendor> vendors;

    public ArrayList<Vendor> getVendors() {
        if (vendors == null) {
            return new ArrayList<Vendor>();
        } else {
            return vendors;
        }
    }
}
