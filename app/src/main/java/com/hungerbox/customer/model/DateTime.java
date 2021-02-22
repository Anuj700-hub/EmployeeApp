package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class DateTime {

    @SerializedName("now")
    public long now;

    public long getNow() {
        return now;
    }
}
