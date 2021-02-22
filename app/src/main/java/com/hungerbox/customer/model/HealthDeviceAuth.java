package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 12/20/17.
 */

public class HealthDeviceAuth {

    @SerializedName("auth_url")
    String authUrl;
    @SerializedName("login_required_type")
    String loginRequiredType;
    @SerializedName("login_required")
    boolean loginRequired;

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getLoginRequiredType() {
        return loginRequiredType;
    }

    public void setLoginRequiredType(String loginRequiredType) {
        this.loginRequiredType = loginRequiredType;
    }

    public boolean getLoginRequired() {
        return loginRequired;
    }

    public void setLoginRequired(boolean loginRequired) {
        this.loginRequired = loginRequired;
    }
}
