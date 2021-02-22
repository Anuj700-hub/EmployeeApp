package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

public class VendorResponseOffline {
    public String error;
    @SerializedName("data")
    public VendorOffline vendor;
}
