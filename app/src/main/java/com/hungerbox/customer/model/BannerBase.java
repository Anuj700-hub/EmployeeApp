package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 1/2/18.
 */

public class BannerBase {
    @SerializedName("banners")
    ArrayList<BannerItem> bannerItems;

    public ArrayList<BannerItem> getBannerItems() {
        if (bannerItems == null)
            bannerItems = new ArrayList<>();
        return bannerItems;
    }

    public void setBannerItems(ArrayList<BannerItem> bannerItems) {
        this.bannerItems = bannerItems;
    }
}
