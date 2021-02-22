package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 11/22/17.
 */

public class BookingsResponse {
    @SerializedName("data")
    ArrayList<BookingDetail> bookings;


    public ArrayList<BookingDetail> getBookings() {
        return bookings;
    }

    public void setBookings(ArrayList<BookingDetail> bookings) {
        this.bookings = bookings;
    }
}
