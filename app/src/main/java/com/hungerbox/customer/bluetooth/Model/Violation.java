package com.hungerbox.customer.bluetooth.Model;

import java.util.ArrayList;

public class Violation {

    private long location_id;
    private ArrayList<Long> timestamps;

    public long getLocation_id() {
        return location_id;
    }

    public void setLocation_id(long location_id) {
        this.location_id = location_id;
    }

    public ArrayList<Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(ArrayList<Long> timestamps) {
        this.timestamps = timestamps;
    }
}
