package com.hungerbox.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchVendorMenuVegFilter extends SearchVendorMenu {
    @SerializedName("is_veg")
    @Expose
    protected Integer isVeg;

    public SearchVendorMenuVegFilter(Integer isVeg) {
        this.isVeg = isVeg;
        setActive(1);
    }
}
