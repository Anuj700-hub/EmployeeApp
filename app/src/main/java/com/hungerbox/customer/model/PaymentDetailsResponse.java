package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sandipanmitra on 4/5/18.
 */

public class PaymentDetailsResponse implements Serializable {
    @SerializedName("data")
    PaymentDetail paymentDetails;

    public PaymentDetail getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetail paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
}
