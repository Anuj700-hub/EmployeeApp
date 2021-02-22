package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by manas on 5/4/18.
 */

public class RedeemMenuResponse {
    @SerializedName("data")
    ArrayList<RedeemProduct> redeemProductArrayList;

    public ArrayList<RedeemProduct> getRedeemProductArrayList() {
        return redeemProductArrayList;
    }

    public void setRedeemProductArrayList(ArrayList<RedeemProduct> redeemProductArrayList) {
        this.redeemProductArrayList = redeemProductArrayList;
    }
}
