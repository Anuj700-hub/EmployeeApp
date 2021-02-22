package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CurrentVendorResponse {
    public static String api_key = "current_vendors";

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
