package com.hungerbox.customer.model;

import java.util.ArrayList;

public class PaymentMethodUpi {

    private ArrayList<PaymentMethod> paymentMethod;

    public PaymentMethodUpi(ArrayList<PaymentMethod> paymentMethod){
        this.paymentMethod = paymentMethod;
    }

    public ArrayList<PaymentMethod> getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(ArrayList<PaymentMethod> paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
