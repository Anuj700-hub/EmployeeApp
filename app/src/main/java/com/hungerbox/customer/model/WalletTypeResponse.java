package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 12/11/17.
 */

public class WalletTypeResponse {
    @SerializedName("data")
    ArrayList<WalletType> walletTypes = new ArrayList<>();

    public ArrayList<WalletType> getWalletTypes() {
        return walletTypes;
    }

    public void setWalletTypes(ArrayList<WalletType> walletTypes) {
        this.walletTypes = walletTypes;
    }
}
