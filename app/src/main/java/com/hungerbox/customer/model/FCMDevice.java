package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 19/10/16.
 */
public class FCMDevice {
    private static final long serialVersionUID = 1l;

    @SerializedName("device_id")
    public String deviceId;
    @SerializedName("firebase_id")
    public String fcmId;

    public String getDeviceId() {
        return deviceId;
    }

    public FCMDevice setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getFcmId() {
        return fcmId;
    }

    public FCMDevice setFcmId(String fcmId) {
        this.fcmId = fcmId;
        return this;
    }
}
