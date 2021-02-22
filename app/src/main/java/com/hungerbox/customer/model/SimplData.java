package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class SimplData {

    @SerializedName("code")
    private String mCode;
    @SerializedName("due_by")
    private Long mDueBy;
    @SerializedName("payment_source")
    private String mPaymentSource;
    @SerializedName("pending_bill")
    private Boolean mPendingBill;
    @SerializedName("redirect_url")
    private String mRedirectUrl;
    @SerializedName("total_amount_due")
    private double mTotalAmountDue;

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public Long getDueBy() {
        return mDueBy;
    }

    public void setDueBy(Long dueBy) {
        mDueBy = dueBy;
    }

    public String getPaymentSource() {
        return mPaymentSource;
    }

    public void setPaymentSource(String paymentSource) {
        mPaymentSource = paymentSource;
    }

    public Boolean getPendingBill() {
        return mPendingBill;
    }

    public void setPendingBill(Boolean pendingBill) {
        mPendingBill = pendingBill;
    }

    public String getRedirectUrl() {
        return mRedirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        mRedirectUrl = redirectUrl;
    }

    public double getTotalAmountDue() {
        return mTotalAmountDue;
    }

    public void setTotalAmountDue(double totalAmountDue) {
        mTotalAmountDue = totalAmountDue;
    }

}
