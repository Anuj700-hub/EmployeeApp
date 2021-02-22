package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by peeyush on 4/8/16.
 */
public class ProductResponse extends RealmObject {

    @SerializedName("data")
    public RealmList<Product> products;

    public RealmList<Product> getProducts() {
        if (products == null)
            return new RealmList<>();
        return products;
    }

    public void setProducts(RealmList<Product> products) {
        this.products = products;
    }
}
