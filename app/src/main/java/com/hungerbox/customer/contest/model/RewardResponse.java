package com.hungerbox.customer.contest.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RewardResponse {

    @SerializedName("total_value")
    @Expose
    private Integer totalValue;
    @SerializedName("user_rewards")
    @Expose
    private List<Reward> userRewards = null;

    public Integer getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Integer totalValue) {
        this.totalValue = totalValue;
    }

    public List<Reward> getUserRewards() {
        return userRewards;
    }

    public void setUserRewards(List<Reward> userRewards) {
        this.userRewards = userRewards;
    }

}
