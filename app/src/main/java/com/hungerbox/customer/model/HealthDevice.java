package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by peeyush on 19/10/16.
 */
public class HealthDevice implements Serializable {
    private static final long serialVersionUID = 1l;

    @SerializedName("device_id")
    public String deviceId;
    @SerializedName("code")
    public String authCode;

    public String getDeviceId() {
        return deviceId;
    }

    public HealthDevice setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
