package com.hungerbox.customer.model.serializer;

import com.google.gson.annotations.SerializedName;

public class CreditData {
    @SerializedName("available_credit_in_paise")
    String availableCredit;


    public String getAvailableCredit() {

        return "" + Double.parseDouble(availableCredit) / 100;
    }

    public void setAvailableCredit(String availableCredit) {
        this.availableCredit = availableCredit;
    }
}
