package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ranjeet on 27,December,2018
 */
public class UserBookmarks {
    @SerializedName("data")
    private List<BookMarkMenu> userBookmarkMenus;

    public List<BookMarkMenu> getUserBookmarkMenus() {
        return userBookmarkMenus;
    }

    public void setUserBookmarkMenus(List<BookMarkMenu> userBookmarkMenus) {
        this.userBookmarkMenus = userBookmarkMenus;
    }
}
