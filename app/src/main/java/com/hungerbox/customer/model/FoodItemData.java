package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by manas on 1/12/17.
 */

public class FoodItemData implements Serializable {
    String name = "";
    @SerializedName("food_time")
    String foodTime = "";
    double calorie = 0;
    double carbs = 0;
    double protein = 0;
    double fats = 0;
    double fibre = 0;
    long calorie_intake_id = 0;
    int quantity;

    public long getCalorie_intake_id() {
        return calorie_intake_id;
    }

    public void setCalorie_intake_id(long calorie_intake_id) {
        this.calorie_intake_id = calorie_intake_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoodTime() {
        return foodTime;
    }

    public void setFoodTime(String foodTime) {
        this.foodTime = foodTime;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getFibre() {
        return fibre;
    }

    public void setFibre(double fibre) {
        this.fibre = fibre;
    }
}
