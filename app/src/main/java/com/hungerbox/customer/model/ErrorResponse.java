package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 12/7/16.
 */
public class ErrorResponse {

    @SerializedName("debug_message")
    public String debugMessage;

    @SerializedName("message")
    public String message;

    @SerializedName("errors")
    public Error error;

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Error getError() {
        if (error == null) {
            error = new Error();
        }
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
