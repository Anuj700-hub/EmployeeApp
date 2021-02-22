package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ranjeet on 17,December,2018
 */
public class BookMarkMenuResponse {

    @SerializedName("data")
    public BookMarkMenuData bookMarkMenuData;

    public BookMarkMenuData getBookMarkMenus() {
        if (bookMarkMenuData == null) bookMarkMenuData = new BookMarkMenuData();
        return bookMarkMenuData;
    }

    public void setBookMarkMenus(BookMarkMenuData bookMarkMenuData) {
        this.bookMarkMenuData = bookMarkMenuData;
    }
}
