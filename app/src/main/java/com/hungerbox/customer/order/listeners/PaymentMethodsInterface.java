package com.hungerbox.customer.order.listeners;

import com.hungerbox.customer.model.PaymentMethod;

/**
 * Created by sandipanmitra on 4/6/18.
 */

public interface PaymentMethodsInterface {
    void delinkPayment(PaymentMethod paymentData, String Operation);

    void linkPayment(PaymentMethod paymentData);

    void rechargeWallet(PaymentMethod paymentMethod);

    void setSelected(boolean checkedState, int position);

    void onMorePaymentOption();
}
