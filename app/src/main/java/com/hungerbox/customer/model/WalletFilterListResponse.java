package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WalletFilterListResponse {

    @SerializedName("data")
    ArrayList<WalletFilterList> walletFilterLists;

    public ArrayList<WalletFilterList> getWalletFilterLists() {
        return walletFilterLists;
    }

    public void setWalletFilterLists(ArrayList<WalletFilterList> walletFilterLists) {
        this.walletFilterLists = walletFilterLists;
    }
}
