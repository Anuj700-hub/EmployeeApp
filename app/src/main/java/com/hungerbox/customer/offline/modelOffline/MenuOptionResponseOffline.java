package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MenuOptionResponseOffline {
    @SerializedName("data")
    public ArrayList<SubProductOffline> subProducts;

    public ArrayList<SubProductOffline> getSubProducts() {
        if (subProducts == null)
            return new ArrayList<>();
        return subProducts;
    }

    public void setSubProducts(ArrayList<SubProductOffline> subProducts) {
        this.subProducts = subProducts;
    }
}
