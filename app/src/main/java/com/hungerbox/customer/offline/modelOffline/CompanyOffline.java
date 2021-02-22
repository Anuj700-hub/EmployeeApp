package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

public class CompanyOffline {
    public long id;
    public String name;
    public String code;
    @SerializedName("wallet_frequency")
    public String walletFrequency;
    @SerializedName("subdomain")
    public String subdomain = "";
    public int active;
    @SerializedName("locations")
    public LocationResponseOffline locationResponse;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public CompanyOffline setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String toString() {
        return name;
    }
}
