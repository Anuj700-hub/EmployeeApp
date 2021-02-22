package com.hungerbox.customer.contest.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CampaignDetailResponse {

    @SerializedName("campaign")
    @Expose
    private Campaign campaign;
    @SerializedName("tasks")
    @Expose
    private List<Task> tasks = null;

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

}