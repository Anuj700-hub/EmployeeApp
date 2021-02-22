package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;

public class VendorsResponseOffline {
    public String error;
    @SerializedName("data")
    public ArrayList<VendorOffline> vendors;

    public ArrayList<VendorOffline> getVendors() {
        if (vendors == null)
            vendors = new ArrayList<>();
        return vendors;
    }

    public void setVendors(ArrayList<VendorOffline> vendors) {
        this.vendors = vendors;
    }
}
