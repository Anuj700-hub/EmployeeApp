package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 1/24/18.
 */

public class HomeBannerResponse {
    @SerializedName("data")
    HomeBanner homeBanner;

    public HomeBanner getHomeBanner() {
        if (homeBanner == null) {
            homeBanner = new HomeBanner();
        }
        return homeBanner;
    }

    public void setHomeBanner(HomeBanner homeBanner) {
        this.homeBanner = homeBanner;
    }
}
