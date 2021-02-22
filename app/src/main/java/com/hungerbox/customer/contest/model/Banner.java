package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

public class Banner {

    @SerializedName("campaign_id")
    private String contestCampaignId;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("template_name")
    private String templateName = "";

    public String getContestCampaignId() {
        return contestCampaignId;
    }

    public void setContestCampaignId(String contestCampaignId) {
        this.contestCampaignId = contestCampaignId;
    }

    public String getImageUrl() {
        if(imageUrl==null)
            return "";
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
