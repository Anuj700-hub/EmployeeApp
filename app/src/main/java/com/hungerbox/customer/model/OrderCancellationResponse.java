package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class OrderCancellationResponse {
    @SerializedName("message")
    String message;

    @SerializedName("status_code")
    int statusCode;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
