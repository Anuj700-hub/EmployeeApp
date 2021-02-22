package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubmitResponse {

    @SerializedName("data")
    @Expose
    private SubmitResponseData data;

    public SubmitResponseData getData() {
        return data;
    }

    public void setData(SubmitResponseData data) {
        this.data = data;
    }

}
