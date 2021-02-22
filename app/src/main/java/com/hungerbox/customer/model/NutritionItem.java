package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by sandipanmitra on 12/4/17.
 */

public class NutritionItem extends RealmObject {
    @SerializedName("menu_id")
    long menuId;
    @SerializedName("nutrition_id")
    long nutritionId;
    @SerializedName("name")
    String name;
    @SerializedName("quantity")
    double quantity;
    @SerializedName("calorie")
    double calorie;
    @SerializedName("protein")
    double protein;
    @SerializedName("carbs")
    double carbs;
    @SerializedName("fats")
    double fats;
    @SerializedName("fibre")
    double fibre;
    @SerializedName("measure_unit")
    String measurUnit;


    public long getMenuId() {
        return menuId;
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
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

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
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

    public String getMeasurUnit() {
        return measurUnit;
    }

    public void setMeasurUnit(String measurUnit) {
        this.measurUnit = measurUnit;
    }


    @Override
    protected NutritionItem clone() {
        NutritionItem nutritionItem = new NutritionItem();
        nutritionItem.quantity = quantity;
        nutritionItem.menuId = menuId;
        nutritionItem.nutritionId = nutritionId;
        nutritionItem.name = name;
        nutritionItem.calorie = calorie;
        nutritionItem.protein = protein;
        nutritionItem.carbs = carbs;
        nutritionItem.fats = fats;
        nutritionItem.fibre = fibre;
        nutritionItem.measurUnit = measurUnit;

        return nutritionItem;
    }
}
