package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

public class ServerStatus {
    @SerializedName("offline")
    private boolean offline;

    public boolean getStatus() {
        return offline;
    }

    public void setStatus(boolean status) {
        this.offline = status;
    }

    @SerializedName("new_endpoint")
    private String newEndpoint;

    public String getNewEndpoint() {
        return newEndpoint;
    }

    public void setNewEndpoint(String newEndpoint) {
        this.newEndpoint = newEndpoint;
    }
}
