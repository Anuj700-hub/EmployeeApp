package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class BookedSlotData implements Serializable {

    @SerializedName("data")
    private ArrayList<BookedSlot> bookedSlots;

    public ArrayList<BookedSlot> getBookedSlots() {
        if(bookedSlots == null)
            return new ArrayList<>();
        return bookedSlots;
    }

    public void setBookedSlots(ArrayList<BookedSlot> bookedSlots) {
        this.bookedSlots = bookedSlots;
    }
}
