package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by manas on 23/11/17.
 */

public class BookingDetail {

    @SerializedName("group_event_start_date")
    public String eventStartDate;
    @SerializedName("group_event_end_date")
    public String eventEndDate;
    @SerializedName("location")
    public String location;
    @SerializedName("booking_id")
    long id;
    @SerializedName("experience_centre")
    String bookingLocation;
    @SerializedName("slots")
    ArrayList<BookingDetailSlot> meetingSlots;


    public ArrayList<BookingDetailSlot> getMeetingSlots() {
        return meetingSlots;
    }

    public void setMeetingSlots(ArrayList<BookingDetailSlot> meetingSlots) {
        this.meetingSlots = meetingSlots;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBookingLocation() {
        return bookingLocation;
    }

    public void setBookingLocation(String bookingLocation) {
        this.bookingLocation = bookingLocation;
    }

}
