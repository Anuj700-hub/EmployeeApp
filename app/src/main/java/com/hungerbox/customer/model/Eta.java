package com.hungerbox.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Eta {

    @SerializedName("data")
    @Expose
    private EtaData data;

    public EtaData getData() {
        return data;
    }

    public void setData(EtaData data) {
        this.data = data;
    }

}
