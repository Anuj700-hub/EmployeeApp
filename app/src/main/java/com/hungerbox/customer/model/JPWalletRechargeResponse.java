package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 4/23/18.
 */

public class JPWalletRechargeResponse {
    @SerializedName("data")
    ArrayList<JPWalletRecharge> jpWalletRecharges;

    public ArrayList<JPWalletRecharge> getJpWalletRecharges() {
        return jpWalletRecharges;
    }

    public void setJpWalletRecharges(ArrayList<JPWalletRecharge> jpWalletRecharges) {
        this.jpWalletRecharges = jpWalletRecharges;
    }
}
