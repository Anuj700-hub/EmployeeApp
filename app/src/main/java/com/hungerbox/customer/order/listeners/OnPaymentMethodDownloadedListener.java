package com.hungerbox.customer.order.listeners;

import com.hungerbox.customer.model.PaymentMethod;

import java.util.ArrayList;

public interface OnPaymentMethodDownloadedListener {
    void handlePaymentMethods(ArrayList<PaymentMethod> paymentMethods);
}
