package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class GenerateChallenge {

    @SerializedName("hb_device_id")
    private long deviceId = 0;

    @SerializedName("challenge_string")
    private String challengeString = null;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getChallengeString() {
        return challengeString;
    }

    public void setChallengeString(String challengeString) {
        this.challengeString = challengeString;
    }
}
