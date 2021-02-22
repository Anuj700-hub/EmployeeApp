package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by manas on 30/11/17.
 * <p>
 * {
 * "user_id": 18636,
 * "weight": "178",
 * "height": "178",
 * "gender": "Male",
 * "target_weight": "80",
 * "lifestyle": "Light exercise"
 * }
 */

public class UserHealth implements Serializable {

    @SerializedName("user_id")
    public long userId;
    public double weight;
    public double height;
    public String gender;
    public String dob;
    public String lifestyle;
    @SerializedName("target_weight")
    public double targetWeight;
    @SerializedName("device_linked")
    public boolean deviceLinked;
    @SerializedName("calorie")
    CalorieDataResponse calorieDataResponse;
    @SerializedName("device_name")
    String deviceName = "";

    public String getAge() {
        return dob;
    }

    public void setAge(String age) {
        this.dob = age;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isDeviceLinked() {
        return deviceLinked;
    }

    public void setDeviceLinked(boolean deviceLinked) {
        this.deviceLinked = deviceLinked;
    }

    public CalorieDataResponse getCalorieDataResponse() {
        return calorieDataResponse;
    }

    public void setCalorieDataResponse(CalorieDataResponse calorieDataResponse) {
        this.calorieDataResponse = calorieDataResponse;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLifestyle() {
        return lifestyle;
    }

    public void setLifestyle(String lifestyle) {
        this.lifestyle = lifestyle;
    }

    public double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }
}
