package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 1/19/17.
 */

public class HealthSearchResponse {

    @SerializedName("data")
    ArrayList<FoodNutritionInfo> searchResponse;

    public ArrayList<FoodNutritionInfo> getSearchResponse() {
        return searchResponse;
    }

    public void setSearchResponse(ArrayList<FoodNutritionInfo> searchResponse) {
        this.searchResponse = searchResponse;
    }
}
