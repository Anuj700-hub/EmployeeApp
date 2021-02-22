package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ContestList {

    @SerializedName("banners")
    private BannerData banners;

    @SerializedName("active_campaigns")
    private ActiveCampaignsData activeCampaigns;

    @SerializedName("past_campaigns")
    private PastCampaignsData pastCampaigns;

    public BannerData getBannersData() {
        return banners;
    }

    public void setBannersData(BannerData banners) {
        this.banners = banners;
    }

    public ActiveCampaignsData getActiveCampaignsData() {
        return activeCampaigns;
    }

    public void setActiveCampaignsData(ActiveCampaignsData activeCampaigns) {
        this.activeCampaigns = activeCampaigns;
    }

    public PastCampaignsData getPastCampaignsData() {
        return pastCampaigns;
    }

    public void setPastCampaignsData(PastCampaignsData pastCampaigns) {
        this.pastCampaigns = pastCampaigns;
    }
}
