package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class WaterMark {


    /**
     * timestamp : 1582820414455
     * object2_id :
     * api_key : current_user
     * object1_id : 856956
     */

    @SerializedName("timestamp")
    private long timeStamp;
    @SerializedName("object2_id")
    private String object2Id;
    @SerializedName("api_key")
    private String apiKey;
    @SerializedName("object1_id")
    private String object1Id;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getObject2Id() {
        return object2Id;
    }

    public void setObject2Id(String object2Id) {
        this.object2Id = object2Id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getObject1Id() {
        return object1Id;
    }

    public void setObject1Id(String object1Id) {
        this.object1Id = object1Id;
    }
}
