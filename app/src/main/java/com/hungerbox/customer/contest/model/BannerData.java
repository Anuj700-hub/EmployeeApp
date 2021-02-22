package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

public class BannerData {

    @SerializedName("data")
    private Banner banners;

    public Banner getBanners() {
        return banners;
    }

    public void setBanners(Banner banners) {
        this.banners = banners;
    }
}
