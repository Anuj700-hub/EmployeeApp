package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductResponseOffline {
    @SerializedName("data")
    public ArrayList<ProductOffline> products;

    public ArrayList<ProductOffline> getProducts() {
        if (products == null)
            return new ArrayList<>();
        return products;
    }

    public void setProducts(ArrayList<ProductOffline> products) {
        this.products = products;
    }
}
