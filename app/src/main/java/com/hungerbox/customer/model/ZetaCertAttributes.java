package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 1/11/18.
 */

public class ZetaCertAttributes {
    @SerializedName("name")
    String name = "HungerBox App to App";
    @SerializedName("allowedIFIs")
    String allowedIFIs = "ZETA_ANY";
    @SerializedName("Environment")
    String Enviroment = "staging";
    @SerializedName("paymentTo")
    String paymentTo = "144501:1:1";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAllowedIFIs() {
        return allowedIFIs;
    }

    public void setAllowedIFIs(String allowedIFIs) {
        this.allowedIFIs = allowedIFIs;
    }

    public String getEnviroment() {
        return Enviroment;
    }

    public void setEnviroment(String enviroment) {
        Enviroment = enviroment;
    }

    public String getPaymentTo() {
        return paymentTo;
    }

    public void setPaymentTo(String paymentTo) {
        this.paymentTo = paymentTo;
    }
}
