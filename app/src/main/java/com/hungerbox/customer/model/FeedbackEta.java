package com.hungerbox.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedbackEta {

    @SerializedName("data")
    @Expose
    private FeedbackEtaData data;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public FeedbackEtaData getData() {
        return data;
    }

    public void setData(FeedbackEtaData data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
