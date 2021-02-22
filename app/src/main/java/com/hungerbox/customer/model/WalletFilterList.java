package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class WalletFilterList {

    @SerializedName("id")
    private int id=0;

    @SerializedName("name")
    private String walletName;

    @SerializedName("display_name")
    private String walletDisplayName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletDisplayName() {
        return walletDisplayName;
    }

    public void setWalletDisplayName(String walletDisplayName) {
        this.walletDisplayName = walletDisplayName;
    }

    @Override
    public String toString() {
        return getWalletDisplayName();
    }
}
