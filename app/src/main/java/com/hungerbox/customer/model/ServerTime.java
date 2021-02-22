package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

public class ServerTime {

    @SerializedName("now")
    public long serverNow;
    public long localNow;
    public long difference;

    public void calculateDifference() {
        localNow  = Calendar.getInstance().getTimeInMillis();
        difference = (serverNow*1000l)-localNow;
    }
}
