package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class OrderCancellationEligibilityResponse {

    @SerializedName("data")
    OrderCancellationEligibilityData data;

    public boolean isDirectCancellable(){
        return data.isDirectCancellable();
    }
    public boolean askForReason(){
        return data.askForReason();
    }

    public String getCancellationMessage(){
        return data.getCancellationMessage();
    }

}
