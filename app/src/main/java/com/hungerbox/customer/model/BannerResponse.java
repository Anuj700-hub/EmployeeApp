package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 1/2/18.
 */

public class BannerResponse {
    @SerializedName("data")
    BannerBase bannerBase;

    public BannerBase getBannerBase() {
        if (bannerBase == null)
            bannerBase = new BannerBase();
        return bannerBase;
    }

    public void setBannerBase(BannerBase bannerBase) {
        this.bannerBase = bannerBase;
    }
}
