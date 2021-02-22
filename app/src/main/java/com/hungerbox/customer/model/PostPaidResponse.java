package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PostPaidResponse implements Serializable {

    @SerializedName("bill_ref")
    String billReference;
    @SerializedName("cycle_start_date")
    String cycleStartDate;
    @SerializedName("cycle_end_date")
    String cycleEndDate;
    @SerializedName("amount")
    double amount;
    @SerializedName("status")
    String status;
    @SerializedName("bill_date")
    String billDate;
    @SerializedName("bill_payment_date")
    String billPaymentDate;
    @SerializedName("transactions")
    Transactions transactions;
    @SerializedName("message")
    String message;
    @SerializedName("force_payment")
    boolean forcePayment = false;

    public boolean isForcePayment() {
        return forcePayment;
    }

    public void setForcePayment(boolean forcePayment) {
        this.forcePayment = forcePayment;
    }

    public String getBillReference() {
        return billReference;
    }

    public void setBillReference(String billReference) {
        this.billReference = billReference;
    }

    public String getCycleStartDate() {
        return cycleStartDate;
    }

    public void setCycleStartDate(String cycleStartDate) {
        this.cycleStartDate = cycleStartDate;
    }

    public String getCycleEndDate() {
        return cycleEndDate;
    }

    public void setCycleEndDate(String cycleEndDate) {
        this.cycleEndDate = cycleEndDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getBillPaymentDate() {
        return billPaymentDate;
    }

    public void setBillPaymentDate(String billPaymentDate) {
        this.billPaymentDate = billPaymentDate;
    }

    public Transactions getTransactions() {
        return transactions;
    }

    public void setTransactions(Transactions transactions) {
        this.transactions = transactions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
