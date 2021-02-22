package com.hungerbox.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedbackEtaData {

    @SerializedName("response-recorded")
    @Expose
    private String responseRecorded;

    public String getResponseRecorded() {
        return responseRecorded;
    }

    public void setResponseRecorded(String responseRecorded) {
        this.responseRecorded = responseRecorded;
    }

}
