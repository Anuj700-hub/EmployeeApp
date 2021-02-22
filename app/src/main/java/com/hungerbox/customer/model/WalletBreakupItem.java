package com.hungerbox.customer.model;


import java.io.Serializable;

public class WalletBreakupItem implements Serializable {

    public String walletName = "";
    public String walletAmmount = "0";

    public WalletBreakupItem(String walletName, String walletAmmount) {
        this.walletName = walletName;
        this.walletAmmount = walletAmmount;
    }

    public String getWalletName() {
        return walletName;
    }

    public String getWalletAmmount() {
        return walletAmmount;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public void setWalletAmmount(String walletAmmount) {
        this.walletAmmount = walletAmmount;
    }
}