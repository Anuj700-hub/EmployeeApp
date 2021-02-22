package com.hungerbox.customer.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResendOtpResponse {

    @SerializedName("data")
    @Expose
    private ResendOtpData resendOtpData;

    public ResendOtpData getResendOtpData() {
        return resendOtpData;
    }

    public void setResendOtpData(ResendOtpData resendOtpData) {
        this.resendOtpData = resendOtpData;
    }

}
