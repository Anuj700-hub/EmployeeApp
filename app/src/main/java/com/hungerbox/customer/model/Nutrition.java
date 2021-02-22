package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by sandipanmitra on 12/4/17.
 */

public class Nutrition extends RealmObject {
    @SerializedName("data")
    RealmList<NutritionItem> nutritionItems;

    public RealmList<NutritionItem> getNutritionItems() {
        if (nutritionItems == null)
            return new RealmList<>();
        return nutritionItems;
    }


    public void setNutritionItems(RealmList<NutritionItem> nutritionItems) {
        this.nutritionItems = nutritionItems;
    }

    public double getTotalCal() {
        double totalCalories = 0.00;
        for (NutritionItem nutritionItem : getNutritionItems()) {
            totalCalories += (nutritionItem.getCalorie() * nutritionItem.getQuantity());
        }
        return totalCalories;
    }
}
