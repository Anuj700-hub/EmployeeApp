package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MilestoneData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("campaign_id")
    @Expose
    private Integer campaignId;
    @SerializedName("total_task_count")
    @Expose
    private Integer totalTaskCount;
    @SerializedName("required_task_count")
    @Expose
    private Integer requiredTaskCount;
    @SerializedName("taskIds")
    @Expose
    private String taskIds;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("tasks_left")
    @Expose
    private Integer taskLeft;
    @SerializedName("unlocked_at")
    @Expose
    private Object unlockedAt;
    @SerializedName("unlock")
    @Expose
    private boolean isUnlocked;
    @SerializedName("last_task_id")
    @Expose
    private Integer lastTaskId;
    @SerializedName("reward")
    @Expose
    private Reward reward;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public Integer getTotalTaskCount() {
        return totalTaskCount;
    }

    public void setTotalTaskCount(Integer totalTaskCount) {
        this.totalTaskCount = totalTaskCount;
    }

    public Integer getRequiredTaskCount() {
        return requiredTaskCount;
    }

    public void setRequiredTaskCount(Integer requiredTaskCount) {
        this.requiredTaskCount = requiredTaskCount;
    }

    public String getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(String taskIds) {
        this.taskIds = taskIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(Object unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public Integer getLastTaskId() {
        return lastTaskId;
    }

    public void setLastTaskId(Integer lastTaskId) {
        this.lastTaskId = lastTaskId;
    }

    public Integer getTaskLeft() {
        return taskLeft;
    }

    public void setTaskLeft(Integer taskLeft) {
        this.taskLeft = taskLeft;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }
}
