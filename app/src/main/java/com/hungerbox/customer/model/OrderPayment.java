package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.util.ApplicationConstants;

import java.io.Serializable;

/**
 * Created by peeyush on 6/9/16.
 */
public class OrderPayment implements Serializable {
    @SerializedName("user_wallet_id")
    long walletId;
    @SerializedName("amount")
    double amount;
    @SerializedName("method")
    String paymentMethodType = "";
    @SerializedName("wallet_id")
    String paymentMethodId = "";
    @SerializedName("bin_id")
    String binId;
//    payment method id is only important in case wallet and saved card


    public OrderPayment() {

    }

    public OrderPayment(long walletId, double amount) {
        this.walletId = walletId;
        this.amount = amount;
    }

    public OrderPayment(PaymentMethod paymentMethod) {
        this(paymentMethod.getId(), paymentMethod.getAmount());
        if (paymentMethod.getPaymentMethod().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_NETBANKING)) {
            paymentMethodType = paymentMethod.getNetBankingMethods().getNetBankingMethods().get(0).getMethod();
        }
        else if(paymentMethod.getPaymentSource()!=null && paymentMethod.getPaymentSource().getPaymentData()!=null){
            paymentMethodType = paymentMethod.getPaymentSource().getPaymentData().getCode();
        }
        else {
            paymentMethodType = paymentMethod.getPaymentMethod();
        }
        paymentMethodId = (paymentMethod.getPaymentDetails() != null) ? paymentMethod.getPaymentDetails().getId() : "";
    }

    public String getBinId() {
        return binId;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
