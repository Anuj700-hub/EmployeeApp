package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class SimplSettlement {

    @SerializedName("data")
    private SimplData mData;

    public SimplData getData() {
        return mData;
    }

    public void setData(SimplData data) {
        mData = data;
    }

}
