package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sandipanmitra on 12/11/17.
 */

public class OrderWallet implements Serializable {
//    "user_id": 18636,
//            "reference_type": "Order",
//            "reference_id": 116057,
//            "before_amount": 95134.72,
//            "change": -84,
//            "after_amount": 95050.72,
//            "comment": "Taken for Order",
//            "created_at": 1512964714,
//            "wallet_name": "Personal Wallet",
//            "wallet_source": "internal"

    @SerializedName("reference_type")
    String referenceType;
    @SerializedName("reference_id")
    String referenceId;
    @SerializedName("change")
    double change;
    @SerializedName("wallet_name")
    String walletName;
    @SerializedName("wallet_source")
    String walletSource;
    @SerializedName("transaction_status")
    String status;
    @SerializedName("transaction_reference_id")
    String transactionId;
    @SerializedName("logo")
    String logoImg = "";

    public String getLogoImg() {
        return logoImg;
    }

    public boolean hasLogoImg(){
        if(logoImg != null && !logoImg.isEmpty()){
            return true;
        }
        return false;
    }

    public void setLogoImg(String logoImg) {
        this.logoImg = logoImg;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletSource() {
        return walletSource;
    }

    public void setWalletSource(String walletSource) {
        this.walletSource = walletSource;
    }

    public String getStatus() {
        if (status == null)
            status = "";
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
