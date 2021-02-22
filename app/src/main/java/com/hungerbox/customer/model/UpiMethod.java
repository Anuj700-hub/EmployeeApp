package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpiMethod implements Serializable {

    @SerializedName("id")
    private long upiId;

    @SerializedName("package_name")
    private String packageName;

    @SerializedName("app_display_name")
    private String appDisplayName;

    @SerializedName("app_icon")
    private String appIcon;

    @SerializedName("alert_message")
    private String alertMessage;

    @SerializedName("priority")
    private long priority;

    @SerializedName("method")
    private String method;

    @SerializedName("payment_offer")
    PaymentOfferResponse paymentOfferResponse;

    private String appInfoPkgName="";
    private String appInfoActivityName="";
    private boolean selected;

    public long getUpiId() {
        return upiId;
    }

    public void setUpiId(long upiId) {
        this.upiId = upiId;
    }

    public String getPackageName() {
        if(packageName==null)
            return "";
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppDisplayName() {
        return appDisplayName;
    }

    public void setAppDisplayName(String appDisplayName) {
        this.appDisplayName = appDisplayName;
    }

    public String getAppIcon() {
        if(appIcon == null)
            return "";
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public String getMethod() {
        if(method==null)
            return "";
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAppInfoActivityName() {
        return appInfoActivityName;
    }

    public void setAppInfoActivityName(String appInfoActivityName) {
        this.appInfoActivityName = appInfoActivityName;
    }

    public String getAppInfoPkgName() {
        return appInfoPkgName;
    }

    public void setAppInfoPkgName(String appInfoPkgName) {
        this.appInfoPkgName = appInfoPkgName;
    }

    public PaymentOfferResponse getPaymentOfferResponse() {
        return paymentOfferResponse;
    }

    public void setPaymentOfferResponse(PaymentOfferResponse paymentOfferResponse) {
        this.paymentOfferResponse = paymentOfferResponse;
    }

    public String getPaymentOfferText() {
        if (paymentOfferResponse == null || paymentOfferResponse.paymentOffer == null) {
            return "";
        } else {
            return paymentOfferResponse.paymentOffer.getDesc();
        }
    }

}
