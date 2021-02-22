package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 1/11/18.
 */

public class ZetaRequestPurpose {

    @SerializedName("purpose")
    String purpose = "FOOD";
    @SerializedName("amount")
    ZetaRequestAmount amount = new ZetaRequestAmount();

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public ZetaRequestAmount getAmount() {
        return amount;
    }

    public void setAmount(ZetaRequestAmount amount) {
        this.amount = amount;
    }
}
