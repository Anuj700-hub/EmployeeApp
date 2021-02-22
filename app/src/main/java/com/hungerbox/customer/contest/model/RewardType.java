package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RewardType {

    @SerializedName("data")
    private ArrayList<RewardDetails> rewadDetails;

    public ArrayList<RewardDetails> getRewadDetails() {
        return rewadDetails;
    }

    public void setRewadDetails(ArrayList<RewardDetails> rewadDetails) {
        this.rewadDetails = rewadDetails;
    }
}
