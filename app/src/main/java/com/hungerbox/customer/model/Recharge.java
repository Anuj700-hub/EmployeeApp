package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 31/8/16.
 */
public class Recharge {

    @SerializedName("amount")
    String amount;

    @SerializedName("origin")
    String origin = "android";


    public String getAmount() {
        return amount;
    }

    public Recharge setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
