package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JusPayPaymentItemModel implements Serializable {

    @SerializedName("key")
    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
