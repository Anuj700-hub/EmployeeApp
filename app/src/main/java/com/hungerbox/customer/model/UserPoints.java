package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manas on 5/4/18.
 * {
 * "wallet_id": 12323,
 * "points": 100
 * }
 */

public class UserPoints {
    @SerializedName("wallet_id")
    long walletId;
    double points;

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
