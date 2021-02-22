package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 6/9/16.
 */
public class UserCardCheck {
    @SerializedName("card_check")
    int cardCheck;

    public int isCardCheck() {
        return cardCheck;
    }

    public UserCardCheck setCardCheck(int cardCheck) {
        this.cardCheck = cardCheck;
        return this;
    }
}
