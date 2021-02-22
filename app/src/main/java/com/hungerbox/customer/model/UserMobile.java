package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 6/9/16.
 */
public class UserMobile {
    @SerializedName("mobile_no")
    String mobileNum;

    public String getMobileNum() {
        return mobileNum;
    }

    public UserMobile setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
        return this;
    }
}
