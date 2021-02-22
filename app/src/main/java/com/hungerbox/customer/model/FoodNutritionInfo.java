package com.hungerbox.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * {
 * "nutrition_id": 7900,
 * "name": "Bread, pan dulce, sweet yeast bread",
 * "calorie": "3.70",
 * "carbs": "0.56",
 * "protein": "0.09",
 * "fats": "0.12",
 * "fibre": "0.02",
 * "measure_name": "grams",
 * "measure_unit": "grams (1 gms)",
 * "measure_weight": "1.00"
 * }
 * Created by manas on 4/12/17.
 */

public class FoodNutritionInfo implements Serializable {
    boolean isAdded = false;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("nutrition_id")
    @Expose
    private long nutritionId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("carbs")
    @Expose
    private String carbs;
    @SerializedName("calorie")
    @Expose
    private double calorie;
    @SerializedName("protein")
    @Expose
    private double protein;
    @SerializedName("fats")
    @Expose
    private double fats;
    @SerializedName("fibre")
    @Expose
    private double fibre;
    @SerializedName("measure_name")
    @Expose
    private String measureName;
    @SerializedName("measure_unit")
    @Expose
    private String measureUnit;
    @SerializedName("measure_weight")
    @Expose
    private String measureWeight;
    private int quantity = 1;

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getNutritionId() {
        return nutritionId;
    }

    public void setNutritionId(long nutritionId) {
        this.nutritionId = nutritionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
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

    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    public String getMeasureWeight() {
        return measureWeight;
    }

    public void setMeasureWeight(String measureWeight) {
        this.measureWeight = measureWeight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
