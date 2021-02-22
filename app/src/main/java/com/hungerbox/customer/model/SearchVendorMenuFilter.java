package com.hungerbox.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchVendorMenuFilter extends SearchVendorMenu {
    @SerializedName("is_veg")
    @Expose
    protected Integer isVeg;
    @SerializedName("is_mrp_item")
    @Expose
    protected Integer isMrpItem;

    public SearchVendorMenuFilter(Integer isVeg, Integer isMrpItem) {
        this.isVeg = isVeg;
        this.isMrpItem = isMrpItem;
        setActive(1);
    }
}
