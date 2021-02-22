package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 4/8/16.
 */
public class UserHealthResponse {

    @SerializedName("data")
    public UserHealth userHealth;

    public UserHealth getUserHealth() {
        return userHealth;
    }

    public void setUserHealth(UserHealth userHealth) {
        this.userHealth = userHealth;
    }
}
