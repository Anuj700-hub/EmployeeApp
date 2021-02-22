package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 1/24/18.
 */

public class HomeBannerItemResponse {

    public static String api_key = "banners";
    @SerializedName("data")
    ArrayList<HomeBannerItem> homeBannerItems;

    public ArrayList<HomeBannerItem> getHomeBannerItems() {
        if (homeBannerItems == null)
            homeBannerItems = new ArrayList<>();
        return homeBannerItems;
    }

    public void setHomeBannerItems(ArrayList<HomeBannerItem> homeBannerItems) {
        this.homeBannerItems = homeBannerItems;
    }
}
