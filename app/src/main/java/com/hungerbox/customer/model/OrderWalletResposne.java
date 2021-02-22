package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sandipanmitra on 12/11/17.
 */

public class OrderWalletResposne implements Serializable {

    @SerializedName("data")
    ArrayList<OrderWallet> orderWallets;

    public ArrayList<OrderWallet> getOrderWallets() {
        if (orderWallets == null)
            orderWallets = new ArrayList<>();
        return orderWallets;
    }

    public void setOrderWallets(ArrayList<OrderWallet> orderWallets) {
        this.orderWallets = orderWallets;
    }
}
