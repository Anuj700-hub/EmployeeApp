package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by manas on 1/12/17.
 * <p>
 * "id": 15,
 * "user_id": 18636,
 * "date": "2017-12-06",
 * "ideal_calorie_intake": 2725,
 * "calorie_intake": "808.90",
 * "protein": "30.84",
 * "carbs": "100.69",
 * "fats": "31.60",
 * "fibre": "14.43",
 * "calorie_burned": "0.00",
 * "created_at": "2017-12-06 00:30:01",
 * "updated_at": "2017-12-06 08:20:25",
 * "steps_taken": null,
 * "bmr": 2271,
 * "ideal_calorie_burn": 2725,
 * "weight": "76.00"
 */

public class CalorieData implements Serializable {
    @SerializedName("ideal_calorie_intake")
    double idealCalorieIntake = 0;
    @SerializedName("calorie_intake")
    double calorieIntake = 0;
    @SerializedName("calorie_burned")
    double calorieBurned = 0;
    @SerializedName("ideal_calorie_burn")
    double idealCalorieBurn = 0;
    double carbs = 0;
    double protein = 0;
    double fats = 0;
    double fibre = 0;
    String date;
    double steps = 0;
    double weight = 0;
    double bmi = 0;
    double bmr = 0;
    ArrayList<FoodItemData> foodItemData;

    public ArrayList<FoodItemData> getFoodItemData() {
        return foodItemData;
    }

    public void setFoodItemData(ArrayList<FoodItemData> foodItemData) {
        this.foodItemData = foodItemData;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getBmr() {
        return bmr;
    }

    public void setBmr(double bmr) {
        this.bmr = bmr;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }

    public double getIdealCalorieBurn() {
        return idealCalorieBurn;
    }

    public void setIdealCalorieBurn(double idealCalorieBurn) {
        this.idealCalorieBurn = idealCalorieBurn;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getIdealCalorieIntake() {
        return idealCalorieIntake;
    }

    public void setIdealCalorieIntake(double idealCalorieIntake) {
        this.idealCalorieIntake = idealCalorieIntake;
    }

    public double getCalorieIntake() {
        return calorieIntake;
    }

    public void setCalorieIntake(double calorieIntake) {
        this.calorieIntake = calorieIntake;
    }

    public double getCalorieBurned() {
        return calorieBurned;
    }

    public void setCalorieBurned(double calorieBurned) {
        this.calorieBurned = calorieBurned;
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
