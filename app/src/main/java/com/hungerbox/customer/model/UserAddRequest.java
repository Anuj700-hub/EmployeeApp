package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 13/10/16.
 */
public class UserAddRequest {
    @SerializedName("username")
    String username;
    @SerializedName("occasionId")
    long occasionId;

    public String getUsername() {
        return username;
    }

    public UserAddRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public long getOccasionId() {
        return occasionId;
    }

    public UserAddRequest setOccasionId(long occasionId) {
        this.occasionId = occasionId;
        return this;
    }
}
