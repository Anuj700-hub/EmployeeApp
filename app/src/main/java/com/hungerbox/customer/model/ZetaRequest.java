package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 1/11/18.
 */

public class ZetaRequest {

    @SerializedName("version")
    String version = "1.0";
    @SerializedName("merchantUA")
    String merchantUA = "Android/NA/zeta_1/1.0.66";
    @SerializedName("requestId")
    String requestId = "req_spar_vbdjkahffoasdh874627wqufid";
    @SerializedName("amount")
    ZetaRequestAmount amount = new ZetaRequestAmount();
    @SerializedName("logoUrl")
    String logoUrl = "http://hungerbox.com/images/hblogo.png";
    @SerializedName("description")
    String description = "Payment for order";
    @SerializedName("callbackUrl")
    String callbackUrl = "";
    @SerializedName("purposes")
    ArrayList<ZetaRequestPurpose> purposes = new ArrayList<>();


    public ZetaRequest() {
        purposes = new ArrayList<>();
        purposes.add(new ZetaRequestPurpose());
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMerchantUA() {
        return merchantUA;
    }

    public void setMerchantUA(String merchantUA) {
        this.merchantUA = merchantUA;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public ZetaRequestAmount getAmount() {
        return amount;
    }

    public void setAmount(ZetaRequestAmount amount) {
        this.amount = amount;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public ArrayList<ZetaRequestPurpose> getPurposes() {
        return purposes;
    }

    public void setPurposes(ArrayList<ZetaRequestPurpose> purposes) {
        this.purposes = purposes;
    }


    public void setOrderId(String orderId) {
        this.requestId = orderId;
    }

    public void setAmountValue(String amount) {
        this.getAmount().setValues(amount);
        if (this.purposes.size() > 0) {
            this.purposes.get(0).amount.setValues(amount);
        }
    }
}
