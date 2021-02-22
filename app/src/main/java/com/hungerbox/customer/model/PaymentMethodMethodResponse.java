package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sandipanmitra on 4/5/18.
 */

public class PaymentMethodMethodResponse implements Serializable {
    @SerializedName("data")
    ArrayList<PaymentMethodMethod> netBankingMethods;

    public ArrayList<PaymentMethodMethod> getNetBankingMethods() {
        if (netBankingMethods == null)
            netBankingMethods = new ArrayList<>();
        return netBankingMethods;
    }

    public void setNetBankingMethods(ArrayList<PaymentMethodMethod> netBankingMethods) {
        this.netBankingMethods = netBankingMethods;
    }
}
