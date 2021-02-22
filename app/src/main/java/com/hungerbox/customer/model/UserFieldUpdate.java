package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 6/9/16.
 */
public class UserFieldUpdate {
    @SerializedName("mobile_no")
    public String mobileNum;

    @SerializedName("email")
    String email;

    @SerializedName("name")
    String name;

    public String getName() {
        return name;
    }

    public UserFieldUpdate setName(String name) {
        this.name = name;
        return this;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public UserFieldUpdate setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserFieldUpdate setEmail(String email) {
        this.email = email;
        return this;
    }
}
