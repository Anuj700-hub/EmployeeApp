package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by manas on 1/12/17.
 */

public class FoodItemDataResponse implements Serializable {
    @SerializedName("data")
    ArrayList<FoodItemData> foodItemData;

    public ArrayList<FoodItemData> getFoodItemData() {
        return foodItemData;
    }

    public void setFoodItemData(ArrayList<FoodItemData> foodItemData) {
        this.foodItemData = foodItemData;
    }
}
