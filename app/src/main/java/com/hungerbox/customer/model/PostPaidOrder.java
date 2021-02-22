package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PostPaidOrder implements Serializable {

    @SerializedName("txn_ref")
    String txnRef;
    @SerializedName("merchant_txn_id")
    String merchantTxnID;
    @SerializedName("merchant_order_id")
    String merchantOrderId;
    @SerializedName("amount")
    double amount;
    @SerializedName("txn_type")
    String txnType;
    @SerializedName("txn_date")
    String txnDate;
    @SerializedName("status")
    String OrderStatus;

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getMerchantTxnID() {
        return merchantTxnID;
    }

    public void setMerchantTxnID(String merchantTxnID) {
        this.merchantTxnID = merchantTxnID;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }
}
