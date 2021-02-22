package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Manas Dadheech on 23/8/17.
 */

public class WalletOtpVerification implements Serializable {

    /*
    {
    "verification_id":"87c5617b-4ecb-472e-8e80-abbbcd64d88b",
    "phone_number":"8892640168"
    }
   */
    @SerializedName("verification_id")
    String verificationId;
    @SerializedName("phone_number")
    String phoneNumber;
    String otp;
    @SerializedName("otp_regex")
    String otpRegex = "";

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtpRegex() {
        return otpRegex;
    }

    public void setOtpRegex(String otpRegex) {
        this.otpRegex = otpRegex;
    }
}
