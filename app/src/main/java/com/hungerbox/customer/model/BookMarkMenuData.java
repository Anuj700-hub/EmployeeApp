package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ranjeet on 27,December,2018
 */
public class BookMarkMenuData {

    @SerializedName("UserBookmarks")
    private UserBookmarks userBookmarks;

    @SerializedName("TrendingMenu")
    private TrendingMenu trendingMenu;

    public void setTrendingMenu(TrendingMenu trendingMenu){
        this.trendingMenu = trendingMenu;
    }

    public TrendingMenu getTrendingMenu(){
        return trendingMenu;
    }

    public void setUserBookmarks(UserBookmarks userBookmarks){
        this.userBookmarks = userBookmarks;
    }

    public UserBookmarks getUserBookmarks(){
        return userBookmarks;
    }

}
