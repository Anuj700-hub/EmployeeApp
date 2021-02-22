package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 7/14/17.
 */

public class RegistrationUserResposne {

    @SerializedName("data")
    RegistrationUser registrationUser;

    public RegistrationUser getRegistrationUser() {
        return registrationUser;
    }

    public void setRegistrationUser(RegistrationUser registrationUser) {
        this.registrationUser = registrationUser;
    }
}
