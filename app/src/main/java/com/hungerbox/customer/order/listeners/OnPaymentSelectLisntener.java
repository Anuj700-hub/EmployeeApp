package com.hungerbox.customer.order.listeners;

import com.hungerbox.customer.model.PaymentMethod;

/**
 * Created by sandipanmitra on 3/23/18.
 */

public interface OnPaymentSelectLisntener {

    void handlePaymentMethodSelected(PaymentMethod paymentMethod, boolean selected);
}
