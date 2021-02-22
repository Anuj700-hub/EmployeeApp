package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 10/18/17.
 */

public class Ticker {
    @SerializedName("comment")
    String ticker;

    public String getTicker() {
        if (ticker == null)
            ticker = "";
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
}
