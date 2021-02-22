package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by manas on 1/12/17.
 */

public class ListCalorieDataResponse implements Serializable {
    @SerializedName("data")
    ArrayList<CalorieData> calorieData;

    public ArrayList<CalorieData> getCalorieData() {
        return calorieData;
    }

    public void setCalorieData(ArrayList<CalorieData> calorieData) {
        this.calorieData = calorieData;
    }
}
