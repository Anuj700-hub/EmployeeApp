package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Manas Dadheech on 17/8/17.
 */
public class ListWallet {

    @SerializedName("data")
    private ArrayList<PaymentMethod> PaymentMethods;

    public ArrayList<PaymentMethod> getPaymentMethods() {
        return PaymentMethods;
    }

    public void setPaymentMethods(ArrayList<PaymentMethod> paymentMethods) {
        PaymentMethods = paymentMethods;
    }
}
