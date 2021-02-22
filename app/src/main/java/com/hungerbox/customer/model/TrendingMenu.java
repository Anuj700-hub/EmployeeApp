package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ranjeet on 27,December,2018
 */
public class TrendingMenu {

    @SerializedName("data")
    private List<TrendingMenuItem> trendingMenus;

    public List<TrendingMenuItem> getTrendingMenus() {
        return trendingMenus;
    }

    public void setTrendingMenus(List<TrendingMenuItem> trendingMenus) {
        this.trendingMenus = trendingMenus;
    }
}
