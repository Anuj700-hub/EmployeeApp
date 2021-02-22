package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 1/11/18.
 */

public class ZetaRequestAmount {
    //    "currency": "INR",
//                "value": "20.00"
    @SerializedName("currency")
    String currency = "INR";
    @SerializedName("value")
    String values = "20.00";

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
