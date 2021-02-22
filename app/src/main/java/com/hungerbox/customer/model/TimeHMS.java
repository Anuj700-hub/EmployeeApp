package com.hungerbox.customer.model;

import androidx.annotation.NonNull;

public class TimeHMS {
    private long hours;
    private long minutes;
    private long seconds;

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%02d",hours)+":"+String.format("%02d",minutes)+":"+String.format("%02d",seconds);
    }
}
