package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manas on 5/6/17.
 */
public class UserEmail {
    @SerializedName("email")
    String email;

    public String getEmail() {
        return email;
    }

    public UserEmail setEmail(String email) {
        this.email = email;
        return this;
    }
}
