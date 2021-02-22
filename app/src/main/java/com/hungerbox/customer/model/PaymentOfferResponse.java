package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sandipanmitra on 4/30/18.
 */

public class PaymentOfferResponse implements Serializable {
    @SerializedName("data")
    public PaymentOffer paymentOffer;
}
