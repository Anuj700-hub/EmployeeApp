package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class OrderCancellationEligibilityData {
    @SerializedName("order_ref")
    String orderRef;
    @SerializedName("user_id")
    String userId;
    @SerializedName("vendor_name")
    String vendorName;
    @SerializedName("status")
    String status;
    @SerializedName("value")
    String value;
    @SerializedName("created_at")
    String createdAt;
    @SerializedName("cancellation_code")
    int cancellation_code;
    @SerializedName("cancellation_message")
    String cancellation_message;
    @SerializedName("order_items")
    String orderItems;

    public boolean isDirectCancellable(){
        if(cancellation_code == 1)
            return true;
        return false;
    }

    public boolean askForReason(){
        if(cancellation_code == 2)
            return true;
        return false;
    }

    public String getCancellationMessage(){
        return cancellation_message;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getCancellation_code() {
        return cancellation_code;
    }

    public void setCancellation_code(int cancellation_code) {
        this.cancellation_code = cancellation_code;
    }

    public String getCancellation_message() {
        return cancellation_message;
    }

    public void setCancellation_message(String cancellation_message) {
        this.cancellation_message = cancellation_message;
    }

    public String getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(String orderItems) {
        this.orderItems = orderItems;
    }

}
