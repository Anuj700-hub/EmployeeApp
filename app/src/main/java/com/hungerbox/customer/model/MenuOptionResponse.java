package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by sandipanmitra on 10/27/16.
 */
public class MenuOptionResponse extends RealmObject {
    @SerializedName("data")
    public RealmList<SubProduct> subProducts;

    public RealmList<SubProduct> getSubProducts() {
        if (subProducts == null)
            return new RealmList<>();
        return subProducts;
    }

    public void setSubProducts(RealmList<SubProduct> subProducts) {
        this.subProducts = subProducts;
    }
}
