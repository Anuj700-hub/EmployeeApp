package com.hungerbox.customer.model;


import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

/**
 * Created by sandipanmitra on 3/7/17.
 */

public class BatteryEventPayload implements VendorEventRegistrable {
    @SerializedName("battery")
    float batteryLevel;
    @SerializedName("is_charging")
    boolean isCharging;
    String time;
    @SerializedName("version")
    String appVersion;
    String deviceId;

    public BatteryEventPayload(float batteryLevel, boolean isCharging, String deviceId) {
        this.batteryLevel = batteryLevel;
        this.isCharging = isCharging;
        this.deviceId = deviceId;
        this.time = String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    public float getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(float batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Override
    public void updateTime(long time) {
        this.time = String.valueOf(time);
    }

    @Override
    public long getLocalTime() {
        try {
            return Long.parseLong(this.time);
        } catch (Exception e) {
            return 0;
        }
    }
}
