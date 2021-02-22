package com.hungerbox.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeatureSearch {

    @SerializedName("data")
    @Expose
    private FeatureSearchData data;

    public FeatureSearchData getData() {
        return data;
    }

    public void setData(FeatureSearchData data) {
        this.data = data;
    }

}