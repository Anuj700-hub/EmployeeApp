package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by peeyush on 20/8/16.
 */
public class BookingHistory implements Serializable {

    public long id;
    @SerializedName("experience_centre")
    public String name = "";
    @SerializedName("image")
    public String image;
    @SerializedName("address")
    public String address = "";
    @SerializedName("address1")
    public String address1 = "";
    @SerializedName("date")
    public String date = "";
    @SerializedName("start_time")
    public String startTime = "";
    @SerializedName("end_time")
    public String endTime = "";
    @SerializedName("booking_id")
    public long bookingId;
    @SerializedName("description")
    public String description = "";
    @SerializedName("booked_slot_status")
    public String bookingSlotStatus;
    @SerializedName("upcoming")
    public int upcoming = 0;
    @SerializedName("created_at")
    public String createdAt = "1510488209";
    @SerializedName("ratings_taken")
    public int ratingsTaken = 0;
    @SerializedName("group_event_start_date")
    public String eventStartDate;
    @SerializedName("group_event_end_date")
    public String eventEndDate;
    @SerializedName("event_image")
    public String eventImage;
    @SerializedName("rating")
    public int rating;
    @SerializedName("event_name")
    public String eventName;
    @SerializedName("booking_status")
    public String bookingStatus;

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public int getRatingsTaken() {
        return ratingsTaken;
    }

    public void setRatingsTaken(int ratingsTaken) {
        this.ratingsTaken = ratingsTaken;
    }

    public Date getCreatedAt() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((Long.parseLong(createdAt) * 1000));
        return calendar.getTime();
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        if (name == null)
            name = "";
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookingSlotStatus() {
        return bookingSlotStatus;
    }

    public void setBookingSlotStatus(String bookingSlotStatus) {
        this.bookingSlotStatus = bookingSlotStatus;
    }

    public int getUpcoming() {
        return upcoming;
    }

    public void setUpcoming(int upcoming) {
        this.upcoming = upcoming;
    }
}
