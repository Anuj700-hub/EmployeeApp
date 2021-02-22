package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class NetworkHeaders {

    @SerializedName("last_updated_at")
    private long lastUpdateAt = 0;

    @SerializedName("X-Hb-Request-Id")
    private String requestId="";

    @SerializedName("current_timestamp")
    private long current_timestamp;


    public long getCurrent_timestamp() {
        return current_timestamp;
    }

    public void setCurrent_timestamp(long current_timestamp) {
        this.current_timestamp = current_timestamp;
    }

    public long getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
