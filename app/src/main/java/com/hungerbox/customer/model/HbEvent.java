package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by sandipanmitra on 4/19/17.
 */

public class HbEvent implements VendorEventRegistrable {
    String time;
    @SerializedName("data")
    HashMap<String, String> dataMap = new HashMap<>();


    public void addDataToMap(String key, String data) {
        dataMap.put(key, data);
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
