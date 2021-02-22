package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sandipanmitra on 4/5/18.
 */

public class PaymentDetail implements Serializable {
    //    "id": "wlt_qh20irh847zn1zya",
//            "wallet": "FREECHARGE",
//            "current_balance": null,
//            "linked": false
//            "exp_month": "06",
//            "reference": "dd6e1991a49fd0a46fc8aebccd9e5e72",
//            "number": "4585-XXXXXXXX-5874",
//            "token": "97dc7114-9564-4541-b672-2ae33527ffb0",
//            "fingerprint": "b0sc0evpaaogo09e7lbdulnbd",
//            "isin": "458546",
//            "exp_year": "2022",
//            "expired": false,
//            "name_on_card": "MANAS DADHEECH",
//            "card_type": "DEBIT",
//            "card_brand": "VISA"
    @SerializedName("id")
    String id;
    @SerializedName("wallet")
    String wallet;
    @SerializedName("current_balance")
    double curretBalance;
    @SerializedName("linked")
    boolean linked;
    @SerializedName("name_on_card")
    String nameOnCard;
    @SerializedName("exp_month")
    String expMonth;
    @SerializedName("exp_year")
    String expYear;
    @SerializedName("token")
    String token;
    @SerializedName("number")
    String cardNumber;
    @SerializedName("card_brand")
    String cardBrand;
    @SerializedName("isin")
    String isin;

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getFormattedCardNumber() {
        String first4 = cardNumber.substring(0, 4);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return first4 + " XXXX XXXX " + last4;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null)
            this.id = "";
        this.id = id;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public double getCurretBalance() {
        return curretBalance;
    }

    public void setCurretBalance(double curretBalance) {
        this.curretBalance = curretBalance;
    }

    public boolean isLinked() {
        return linked;
    }

    public void setLinked(boolean linked) {
        this.linked = linked;
    }
}
