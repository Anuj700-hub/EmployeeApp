package com.hungerbox.customer.ksnetwork;

import com.google.gson.annotations.SerializedName;

public class KSErrorResponse {

    @SerializedName("debug_message")
    public String debugMessage;

    @SerializedName("message")
    public String message;

    @SerializedName("errors")
    public Error error;

    @SerializedName("error_code")
    public String errorCode ;

    @SerializedName("status_code")
    public String statusCode;


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

    public String getErrorCode() {
        if(errorCode == null)
            return "-1";
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getStatusCode() {
        if(statusCode == null)
            return "-1";
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
