package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PaymentUpiMethods implements Serializable {
    @SerializedName("data")
    ArrayList<UpiMethod> upiMethods;

    public ArrayList<UpiMethod> getUpiMethods() {
        if (upiMethods==null)
            return new ArrayList<UpiMethod>();
        return upiMethods;
    }

    public void setUpiMethods(ArrayList<UpiMethod> upiMethods) {
        this.upiMethods = upiMethods;
    }
}
