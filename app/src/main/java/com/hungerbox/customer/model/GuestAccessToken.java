package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.network.UrlConstant;

public class GuestAccessToken {

    @SerializedName("grant_type")
    private String grantType = "password";
    @SerializedName("client_id")
    private String clientId = UrlConstant.CLIENT_ID;
    @SerializedName("client_secret")
    private String clientSecret = UrlConstant.CLIENT_SECRET;
    @SerializedName("hb_device_id")
    private long hbDeviceId = -1;
    @SerializedName("challenge_signature")
    private String challengeSignature = null;
    @SerializedName("company_id")
    private long companyId;
    @SerializedName("location_id")
    private long locationId;

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public long getHbDeviceId() {
        return hbDeviceId;
    }

    public void setHbDeviceId(long hbDeviceId) {
        this.hbDeviceId = hbDeviceId;
    }

    public String getChallengeSignature() {
        return challengeSignature;
    }

    public void setChallengeSignature(String challengeSignature) {
        this.challengeSignature = challengeSignature;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }
}

