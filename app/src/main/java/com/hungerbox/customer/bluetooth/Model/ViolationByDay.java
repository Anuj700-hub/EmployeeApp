package com.hungerbox.customer.bluetooth.Model;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.R;

import java.util.ArrayList;

public class ViolationByDay {

    @SerializedName("count")
    int totalViolations;
    @SerializedName("date")
    String violationDayText;
    @SerializedName("percentile")
    double betterThanUserPercentage;
    @SerializedName("detail")
    ArrayList<ViolationByHour> violationByHours;

    public int getUserViolationRangeColor() {
        if (getBetterThanUserPercentage() >= 76.0) {
            return R.color.dark_green;
        } else if (getBetterThanUserPercentage() >= 51.0) {
            return R.color.green;
        } else if (getBetterThanUserPercentage() >= 26.0) {
            return R.color.mustard;
        } else {
            return R.color.violation_red;
        }
    }

    public int getTotalViolations() {
        return totalViolations;
    }

    public void setTotalViolations(int totalViolations) {
        this.totalViolations = totalViolations;
    }

    public String getViolationDayText() {
        return violationDayText;
    }

    public void setViolationDayText(String violationDayText) {
        violationDayText = violationDayText;
    }

    public double getBetterThanUserPercentage() {
        return betterThanUserPercentage;
    }

    public void setBetterThanUserPercentage(double betterThanUserPercentage) {
        this.betterThanUserPercentage = betterThanUserPercentage;
    }

    public ArrayList<ViolationByHour> getViolationByHours() {
        if(violationByHours == null)
            return new ArrayList<>();
        return violationByHours;
    }

    public void setViolationByHours(ArrayList<ViolationByHour> violationByHours) {
        this.violationByHours = violationByHours;
    }
}
