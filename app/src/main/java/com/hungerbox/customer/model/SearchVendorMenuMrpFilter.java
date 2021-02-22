package com.hungerbox.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchVendorMenuMrpFilter extends SearchVendorMenu {
    @SerializedName("is_mrp_item")
    @Expose
    protected Integer isMrpItem;

    public SearchVendorMenuMrpFilter(Integer isMrpItem) {
        this.isMrpItem = isMrpItem;
        setActive(1);
    }
}
