package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sandipanmitra on 1/11/18.
 */

public class ZetaCertPurPoseAttributes implements Serializable {
    @SerializedName("appToApp")
    HashMap<String, String> appToApp = new HashMap<>();

    public HashMap<String, String> getAppToApp() {
        return appToApp;
    }

    public void setAppToApp(HashMap<String, String> appToApp) {
        this.appToApp = appToApp;
    }
}
