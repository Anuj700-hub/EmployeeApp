package com.hungerbox.customer.bluetooth.Model;

import com.google.gson.annotations.SerializedName;

public class ViolationByHour {

    @SerializedName("count")
    int violationsCount;
    @SerializedName("range")
    String timeRange;

    public int getViolationsCount() {
        return violationsCount;
    }

    public void setViolationsCount(int violationsCount) {
        this.violationsCount = violationsCount;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }
}
