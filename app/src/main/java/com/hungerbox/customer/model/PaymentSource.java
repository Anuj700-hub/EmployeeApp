package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by manas on 17/8/17.
 * <p>
 * "data": {
 * "display_name": "Simpl",
 * "code": "simpl",
 * "show_balance": 1,
 * "linking_required": 1,
 * "otp_required_for_linking": 1,
 * "otp_required_for_checkout": 0,
 * "is_user_linked": true
 * }
 */
public class PaymentSource implements Serializable {

    @SerializedName("data")
    PaymentData paymentData;

    public PaymentData getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(PaymentData paymentData) {
        this.paymentData = paymentData;
    }
}
