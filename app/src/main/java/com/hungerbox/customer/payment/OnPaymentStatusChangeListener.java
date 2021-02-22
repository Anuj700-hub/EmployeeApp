package com.hungerbox.customer.payment;

/**
 * Created by sandipanmitra on 4/17/18.
 */

public interface OnPaymentStatusChangeListener {
    void onPaymentStatusInit();

    void onPaymentComplete(boolean status);

    void onPaymentAborted();

}
