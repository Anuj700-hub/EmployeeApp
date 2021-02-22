package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NutritionOffline {
    @SerializedName("data")
    ArrayList<NutritionItemOffline> nutritionItems;

    public ArrayList<NutritionItemOffline> getNutritionItems() {
        if (nutritionItems == null)
            return new ArrayList<>();
        return nutritionItems;
    }


    public void setNutritionItems(ArrayList<NutritionItemOffline> nutritionItems) {
        this.nutritionItems = nutritionItems;
    }

    public double getTotalCal() {
        double totalCalories = 0.00;
        for (NutritionItemOffline nutritionItem : getNutritionItems()) {
            totalCalories += (nutritionItem.getCalorie() * nutritionItem.getQuantity());
        }
        return totalCalories;
    }
}
