package com.hungerbox.customer.model;

import java.util.Calendar;

/**
 * Created by peeyush on 30/8/16.
 */
public class OrderSelectedTime {

    long time;
    boolean now;


    public OrderSelectedTime(long time, boolean now) {
        this.time = time;
        this.now = now;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isNow() {
        return now;
    }

    public void setNow(boolean now) {
        this.now = now;
    }

    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String returnTime = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", calendar.get(Calendar.MINUTE));
        if (now == true)
            return returnTime + "(now)";

        return returnTime;
    }
}
