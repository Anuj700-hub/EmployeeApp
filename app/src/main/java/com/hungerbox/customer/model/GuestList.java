package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by manas on 28/11/16.
 */

public class GuestList {
    @SerializedName("cafe")
    public String cafe;
    @SerializedName("guests")
    private ArrayList<Guests> guestList;

    public String getCafe() {
        return cafe;
    }

    public void setCafe(String cafe) {
        this.cafe = cafe;
    }

    public ArrayList<Guests> getGuestList() {
        return guestList;
    }

    public void setGuestList(ArrayList<Guests> guestList) {
        this.guestList = guestList;
    }
}
