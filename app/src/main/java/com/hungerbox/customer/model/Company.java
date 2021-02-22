package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 20/8/16.
 */
public class Company {

    public long id;
    public String name;
    public String code;
    @SerializedName("wallet_frequency")
    public String walletFrequency;
    @SerializedName("subdomain")
    public String subdomain = "";
    public int active;
    @SerializedName("locations")
    public LocationResponse locationResponse;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public Company setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){ this.name = name;}

    @Override
    public String toString() {
        return name;
    }
}
