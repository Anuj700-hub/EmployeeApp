package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by peeyush on 20/8/16.
 */
public class BookingHistoryResponse {

    @SerializedName("data")
    public ArrayList<BookingHistory> bookingHistories;

    public ArrayList<BookingHistory> getBookingHistories() {
        return bookingHistories;
    }

    public void setBookingHistories(ArrayList<BookingHistory> bookingHistories) {
        this.bookingHistories = bookingHistories;
    }
}
