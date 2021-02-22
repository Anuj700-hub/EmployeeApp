package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by manas on 1/12/17.
 */

public class CalorieDataResponse implements Serializable {
    @SerializedName("data")
    CalorieData calorieData;

    public CalorieData getCalorieData() {
        return calorieData;
    }

    public void setCalorieData(CalorieData calorieData) {
        this.calorieData = calorieData;
    }
}
