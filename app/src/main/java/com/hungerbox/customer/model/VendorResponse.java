package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 2/7/16.
 */
public class VendorResponse {
    public String error;
    @SerializedName("data")
    public Vendor vendor;
}
