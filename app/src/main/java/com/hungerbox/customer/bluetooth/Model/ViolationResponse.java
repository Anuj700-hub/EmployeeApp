package com.hungerbox.customer.bluetooth.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ViolationResponse  {

    @SerializedName("data")
    ArrayList<ViolationByDay> violationByDays;

    public ArrayList<ViolationByDay> getViolationByDays() {
        if(violationByDays == null)
            return new ArrayList<>();
        return violationByDays;
    }

    public void setViolationByDays(ArrayList<ViolationByDay> violationByDays) {
        this.violationByDays = violationByDays;
    }
}
