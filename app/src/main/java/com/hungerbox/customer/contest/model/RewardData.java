package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

public class RewardData {

    @SerializedName("total_rewards")
    private int totalRewards;

    @SerializedName("reward_types")
    private RewardType rewardType;

    public int getTotalRewards() {
        return totalRewards;
    }

    public void setTotalRewards(int totalRewards) {
        this.totalRewards = totalRewards;
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }

}

