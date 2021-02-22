package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class BigBasketGenerateSession {

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("internal_wallet_used")
    public String internal_wallet_used;

    @SerializedName("location_id")
    public String location_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getInternal_wallet_used() {
        return internal_wallet_used;
    }

    public void setInternal_wallet_used(String internal_wallet_used) {
        this.internal_wallet_used = internal_wallet_used;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getOccasion_id() {
        return occasion_id;
    }

    public void setOccasion_id(String occasion_id) {
        this.occasion_id = occasion_id;
    }

    public String getPayment_method_id() {
        return payment_method_id;
    }

    public void setPayment_method_id(String payment_method_id) {
        this.payment_method_id = payment_method_id;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SerializedName("occasion_id")
    public String occasion_id;

    @SerializedName("payment_method_id")
    public String payment_method_id;

    @SerializedName("user_token")
    public String user_token;

    @SerializedName("updated_at")
    public String updated_at;

    @SerializedName("created_at")
    public String created_at;

    @SerializedName("id")
    public String id;

}
