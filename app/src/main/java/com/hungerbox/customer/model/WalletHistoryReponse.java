package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WalletHistoryReponse implements java.io.Serializable {
    @SerializedName("data")
    public ArrayList<WalletHistory> walletHistories;

    public ArrayList<WalletHistory> getWalletHistories() {
        if (walletHistories == null)
            walletHistories = new ArrayList<>();
        return walletHistories;
    }

    public void setWalletHistories(ArrayList<WalletHistory> walletHistories) {
        this.walletHistories = walletHistories;
    }
}
