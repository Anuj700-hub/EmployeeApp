package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CampaignData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("expires_in")
    @Expose
    private String expiresIn;
    @SerializedName("tasks_summary_count")
    @Expose
    private TasksSummaryCount tasksSummaryCount;
    @SerializedName("tasks")
    @Expose
    private Task tasks;
    @SerializedName("milestones")
    @Expose
    private Milestones milestones;
    @SerializedName("terms_and_conditions")
    @Expose
    private String termsAndConditions;
    @SerializedName("campaign_reward_type")
    @Expose
    private String campaignRewardType;
    @SerializedName("end_date")
    @Expose
    private Long endDate;

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public TasksSummaryCount getTasksSummaryCount() {
        return tasksSummaryCount;
    }

    public void setTasksSummaryCount(TasksSummaryCount tasksSummaryCount) {
        this.tasksSummaryCount = tasksSummaryCount;
    }

    public Task getTasks() {
        return tasks;
    }

    public void setTasks(Task tasks) {
        this.tasks = tasks;
    }

    public Milestones getMilestones() {
        return milestones;
    }

    public void setMilestones(Milestones milestones) {
        this.milestones = milestones;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public String getCampaignRewardType() {
        return campaignRewardType;
    }

    public void setCampaignRewardType(String campaignRewardType) {
        this.campaignRewardType = campaignRewardType;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
}
