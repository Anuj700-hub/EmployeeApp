package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

public class PastCampaigns {

    @SerializedName("id")
    private long contestId;

    @SerializedName("title")
    private String contestTitle;

    @SerializedName("logo")
    private String contestLogo;

    @SerializedName("description")
    private String contestDescription;

    @SerializedName("end_date")
    private long contestExpiry;

    @SerializedName("tasks_summary_count")
    private ContestTasks contestTasks;

    @SerializedName("template_name")
    private String templateName;

    @SerializedName("reward")
    private String reward;

    @SerializedName("milestones_count")
    private ContestMilestones contestMilestones;

    public long getContestId() {
        return contestId;
    }

    public void setContestId(long contestId) {
        this.contestId = contestId;
    }

    public String getContestTitle() {
        if(contestTitle == null)
            return "";
        return contestTitle;
    }

    public void setContestTitle(String contestTitle) {
        this.contestTitle = contestTitle;
    }

    public String getContestLogo() {
        if(contestLogo==null)
            return "";
        return contestLogo;
    }

    public void setContestLogo(String contestLogo) {
        this.contestLogo = contestLogo;
    }

    public String getContestDescription() {
        if(contestDescription == null)
            return "";
        return contestDescription;
    }

    public void setContestDescription(String contestDescription) {
        this.contestDescription = contestDescription;
    }

    public long getContestExpiry() {
        return contestExpiry;
    }

    public void setContestExpiry(long contestExpiry) {
        this.contestExpiry = contestExpiry;
    }

    public ContestTasks getContestTasks() {
        return contestTasks;
    }

    public void setContestTasks(ContestTasks contestTasks) {
        this.contestTasks = contestTasks;
    }

    public ContestMilestones getContestMilestones() {
        return contestMilestones;
    }

    public void setContestMilestones(ContestMilestones contestMilestones) {
        this.contestMilestones = contestMilestones;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
