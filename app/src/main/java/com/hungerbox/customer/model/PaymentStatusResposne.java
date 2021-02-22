package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by peeyush on 1/9/16.
 */
public class PaymentStatusResposne implements Serializable {

    @SerializedName("data")
    public PaymentStatus paymentStatus;
}
