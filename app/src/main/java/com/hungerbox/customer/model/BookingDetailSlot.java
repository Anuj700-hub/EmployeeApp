package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 11/22/17.
 */

public class BookingDetailSlot {

    @SerializedName("booked_slot_id")
    long id;
    @SerializedName("start_time")
    String startTime;
    @SerializedName("end_time")
    String endTime;
    @SerializedName("date")
    String humanDate;
    @SerializedName("status")
    String available;
    @SerializedName("can_cancel")
    int cancelable;

    public int getCancelable() {
        return cancelable;
    }

    public boolean isCancelable() {
        if (cancelable == 1)
            return true;
        else
            return false;
    }

    public void setCancelable(int cancelable) {
        this.cancelable = cancelable;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getHumanDate() {
        return humanDate;
    }

    public void setHumanDate(String humanDate) {
        this.humanDate = humanDate;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
