package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

public class RewardDetails {

    @SerializedName("reward_type")
    private String rewardType;

    @SerializedName("icon_image")
    private String iconImage;

    @SerializedName("reward_value")
    private int rewardValue;

    public String getRewardType() {
        if(rewardType==null){
            return "";
        }
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public String getIconImage() {
        if(iconImage==null)
            return "";
        return iconImage;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
    }

    public int getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(int rewardValue) {
        this.rewardValue = rewardValue;
    }
}
