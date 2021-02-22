package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 31/8/16.
 */
public class RechargeResponse {
    @SerializedName("redirect_url")
    String redirectUrl;
    @SerializedName("order_id")
    String orderId;
    @SerializedName("merchant_id")
    String maerchantId;

    public boolean isUseUrl() {
        return useUrl;
    }

    public void setUseUrl(boolean useUrl) {
        this.useUrl = useUrl;
    }

    @SerializedName("useUrl")
    boolean useUrl;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMaerchantId() {
        return maerchantId;
    }

    public void setMaerchantId(String maerchantId) {
        this.maerchantId = maerchantId;
    }
}
